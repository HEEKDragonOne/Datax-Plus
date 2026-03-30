package com.alibaba.datax.plugin.rdbms.writer.util;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.plugin.rdbms.util.DBUtil;
import com.alibaba.datax.plugin.rdbms.util.DBUtilErrorCode;
import com.alibaba.datax.plugin.rdbms.util.DataBaseType;
import com.alibaba.datax.plugin.rdbms.util.RdbmsException;
import com.alibaba.datax.plugin.rdbms.writer.Constant;
import com.alibaba.datax.plugin.rdbms.writer.Key;
import com.alibaba.druid.sql.parser.ParserException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public final class WriterUtil {
    private static final Logger LOG = LoggerFactory.getLogger(WriterUtil.class);

    //TODO 切分报错
    public static List<Configuration> doSplit(Configuration simplifiedConf,
                                              int adviceNumber) {

        List<Configuration> splitResultConfigs = new ArrayList<Configuration>();

        int tableNumber = simplifiedConf.getInt(Constant.TABLE_NUMBER_MARK);

        //处理单表的情况
        if (tableNumber == 1) {
            //由于在之前的  master prepare 中已经把 table,jdbcUrl 提取出来，所以这里处理十分简单
            for (int j = 0; j < adviceNumber; j++) {
                splitResultConfigs.add(simplifiedConf.clone());
            }

            return splitResultConfigs;
        }

        if (tableNumber != adviceNumber) {
            throw DataXException.asDataXException(DBUtilErrorCode.CONF_ERROR,
                    String.format("您的配置文件中的列配置信息有误. 您要写入的目的端的表个数是:%s , 但是根据系统建议需要切分的份数是：%s. 请检查您的配置并作出修改.",
                            tableNumber, adviceNumber));
        }

        String jdbcUrl;
        List<String> preSqls = simplifiedConf.getList(Key.PRE_SQL, String.class);
        List<String> postSqls = simplifiedConf.getList(Key.POST_SQL, String.class);

        List<Object> conns = simplifiedConf.getList(Constant.CONN_MARK,
                Object.class);

        for (Object conn : conns) {
            Configuration sliceConfig = simplifiedConf.clone();

            Configuration connConf = Configuration.from(conn.toString());
            jdbcUrl = connConf.getString(Key.JDBC_URL);
            sliceConfig.set(Key.JDBC_URL, jdbcUrl);

            sliceConfig.remove(Constant.CONN_MARK);

            List<String> tables = connConf.getList(Key.TABLE, String.class);

            for (String table : tables) {
                Configuration tempSlice = sliceConfig.clone();
                tempSlice.set(Key.TABLE, table);
                tempSlice.set(Key.PRE_SQL, renderPreOrPostSqls(preSqls, table));
                tempSlice.set(Key.POST_SQL, renderPreOrPostSqls(postSqls, table));

                splitResultConfigs.add(tempSlice);
            }

        }

        return splitResultConfigs;
    }

    public static List<String> renderPreOrPostSqls(List<String> preOrPostSqls, String tableName) {
        if (null == preOrPostSqls) {
            return Collections.emptyList();
        }

        List<String> renderedSqls = new ArrayList<String>();
        for (String sql : preOrPostSqls) {
            //preSql为空时，不加入执行队列
            if (StringUtils.isNotBlank(sql)) {
                renderedSqls.add(sql.replace(Constant.TABLE_NAME_PLACEHOLDER, tableName));
            }
        }

        return renderedSqls;
    }

    public static void executeSqls(Connection conn, List<String> sqls, String basicMessage, DataBaseType dataBaseType) {
        Statement stmt = null;
        String currentSql = null;
        try {
            stmt = conn.createStatement();
            for (String sql : sqls) {
                currentSql = sql;
                DBUtil.executeSqlWithoutResultSet(stmt, sql);
            }
        } catch (Exception e) {
            throw RdbmsException.asQueryException(dataBaseType, e, currentSql, null, null);
        } finally {
            DBUtil.closeDBResources(null, stmt, null);
        }
    }

    public static String getWriteTemplate(List<String> columnHolders, List<String> valueHolders, String writeMode, DataBaseType dataBaseType, boolean forceUseUpdate) {
        boolean isWriteModeLegal = writeMode.trim().toLowerCase().startsWith("insert")
                || writeMode.trim().toLowerCase().startsWith("replace")
                || writeMode.trim().toLowerCase().startsWith("update");

        if (!isWriteModeLegal) {
            throw DataXException.asDataXException(DBUtilErrorCode.ILLEGAL_VALUE,
                    String.format("您所配置的 writeMode:%s 错误. 因为DataX 目前仅支持replace,update 或 insert 方式. 请检查您的配置并作出修改.", writeMode));
        }
        // && writeMode.trim().toLowerCase().startsWith("replace")
        String writeDataSqlTemplate;
        if (forceUseUpdate ||
                ((dataBaseType == DataBaseType.MySql || dataBaseType == DataBaseType.Tddl) && writeMode.trim().toLowerCase().startsWith("update"))
        ) {
            //update只在mysql下使用

            writeDataSqlTemplate = new StringBuilder()
                    .append("INSERT INTO %s (").append(StringUtils.join(columnHolders, ","))
                    .append(") VALUES(").append(StringUtils.join(valueHolders, ","))
                    .append(")")
                    .append(onDuplicateKeyUpdateString(columnHolders))
                    .toString();
        } else if (dataBaseType == DataBaseType.PostgreSQL
                && writeMode.trim().toLowerCase().startsWith("update")) {
            writeDataSqlTemplate = new StringBuilder()
                    .append("INSERT INTO %s (")
                    .append(StringUtils.join(columnHolders, ","))
                    .append(") VALUES(")
                    .append(StringUtils.join(valueHolders, ","))
                    .append(")")
                    .append(onConFlictDoString(writeMode, columnHolders))
                    .toString();
        } else {
            if (dataBaseType == DataBaseType.DaMeng) {
                if (writeMode.trim().toLowerCase().startsWith("insert")) {
                    writeDataSqlTemplate = new StringBuilder().append(writeMode)
                            .append(" INTO %s (").append(StringUtils.join(columnHolders, ","))
                            .append(") VALUES(").append(StringUtils.join(valueHolders, ","))
                            .append(")").toString();
                } else {
                    writeDataSqlTemplate = onDuplicateKeyMergeString(writeMode, columnHolders);
                }
            } else {

                //这里是保护,如果其他错误的使用了update,需要更换为replace
                if (writeMode.trim().toLowerCase().startsWith("update")) {
                    writeMode = "replace";
                }
                writeDataSqlTemplate = new StringBuilder().append(writeMode)
                        .append(" INTO %s (").append(StringUtils.join(columnHolders, ","))
                        .append(") VALUES(").append(StringUtils.join(valueHolders, ","))
                        .append(")").toString();
            }
        }

        return writeDataSqlTemplate;
    }

    public static String onDuplicateKeyMergeString(String writeMode, List<String> columnHolders) {
        //构建 USING 子查询部分（带占位符）
        StringJoiner srcColumns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (String column : columnHolders) {
            srcColumns.add("? AS " + column);
            placeholders.add("?");
        }

        //构建 MERGE INTO
        StringBuilder sqlBuilder = new StringBuilder().append("MERGE INTO %s as tgt USING (SELECT ")
                .append(srcColumns)
                .append(" FROM DUAL) AS src ");

        // 构建 ON 条件（唯一索引匹配）
        List<String> uniqueColumns = parseUniqueColumns(writeMode);
        // 验证唯一索引字段有效性
        for (String col : uniqueColumns) {
            if (!columnHolders.contains(col)) {
                throw new IllegalArgumentException("Unique column '" + col + "' not found in field list");
            }
        }
        sqlBuilder.append(" ON ( ");
        StringJoiner onConditions = new StringJoiner(" AND ");
        for (String column : uniqueColumns) {
            onConditions.add("tgt." + column + " = src." + column);
        }
        sqlBuilder.append(onConditions).append(") ");

        // 构建 UPDATE SET 部分（排除唯一索引列）
        sqlBuilder.append(" WHEN MATCHED THEN UPDATE SET ");
        StringJoiner updateSet = new StringJoiner(", ");
        for (String column : columnHolders) {
            //if (!column.equals(uniqueColumn)) {
            if (!uniqueColumns.contains(column)) {
                updateSet.add(" tgt." + column + " = src." + column);
            }
        }
        sqlBuilder.append(updateSet);

        // 构建 INSERT 部分
        sqlBuilder.append(" WHEN NOT MATCHED THEN INSERT ");
        StringJoiner insertColumns = new StringJoiner(", ", " (", ")");
        StringJoiner insertValues = new StringJoiner(", ", " VALUES (", ");");
        for (String column : columnHolders) {
            insertColumns.add(column);
            insertValues.add("src." + column);
        }
        sqlBuilder.append(insertColumns).append(insertValues);

        return sqlBuilder.toString();
    }

    /**
     * 解析 writeMode 配置获取唯一索引字段列表
     *
     * @param writeMode 配置字符串，格式示例：
     *                  "update(id)"              -> 单字段唯一索引
     *                  "update(id,user_id)"      -> 多字段组合唯一索引
     *                  "update( "id" , user_id)" -> 带空格的复杂格式
     *                  null 或空值                 -> 使用默认字段名"id"
     */
    private static List<String> parseUniqueColumns(String writeMode) {
        // 默认使用 "id" 作为唯一索引
        if (StringUtils.isBlank(writeMode)) {
            return Collections.singletonList("id");
        }

        // 提取括号内的内容
        String content = writeMode
                .replaceFirst(".*?\\(", "")  // 移除前缀和左括号
                .replaceAll("\\)$", "");     // 移除右括号

        // 分割多个字段并清理空白
        return Arrays.stream(content.split(","))
                .map(String::trim)           // 移除前后空格
                .filter(s -> !s.isEmpty())   // 过滤空字段
                .collect(Collectors.toList());
    }

    public static String onDuplicateKeyUpdateString(List<String> columnHolders) {
        if (columnHolders == null || columnHolders.size() < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ON DUPLICATE KEY UPDATE ");
        boolean first = true;
        for (String column : columnHolders) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(column);
            sb.append("=VALUES(");
            sb.append(column);
            sb.append(")");
        }

        return sb.toString();
    }


    public static String onConFlictDoString(String conflict, List<String> columnHolders) {
        conflict = conflict.replace("update", "").trim();
        StringBuilder sb = new StringBuilder();
        sb.append(" ON CONFLICT ");

        if (!conflict.isEmpty()) {
            sb.append("(").append(conflict).append(")");
        }

        sb.append(" DO UPDATE SET ");

        boolean first = true;
        for (String column : columnHolders) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(column).append("=EXCLUDED.").append(column);
        }

        return sb.toString();
    }

    public static void preCheckPrePareSQL(Configuration originalConfig, DataBaseType type) {
        List<Object> conns = originalConfig.getList(Constant.CONN_MARK, Object.class);
        Configuration connConf = Configuration.from(conns.get(0).toString());
        String table = connConf.getList(Key.TABLE, String.class).get(0);

        List<String> preSqls = originalConfig.getList(Key.PRE_SQL,
                String.class);
        List<String> renderedPreSqls = WriterUtil.renderPreOrPostSqls(
                preSqls, table);

        if (null != renderedPreSqls && !renderedPreSqls.isEmpty()) {
            LOG.info("Begin to preCheck preSqls:[{}].",
                    StringUtils.join(renderedPreSqls, ";"));
            for (String sql : renderedPreSqls) {
                try {
                    DBUtil.sqlValid(sql, type);
                } catch (ParserException e) {
                    throw RdbmsException.asPreSQLParserException(type, e, sql);
                }
            }
        }
    }

    public static void preCheckPostSQL(Configuration originalConfig, DataBaseType type) {
        List<Object> conns = originalConfig.getList(Constant.CONN_MARK, Object.class);
        Configuration connConf = Configuration.from(conns.get(0).toString());
        String table = connConf.getList(Key.TABLE, String.class).get(0);

        List<String> postSqls = originalConfig.getList(Key.POST_SQL,
                String.class);
        List<String> renderedPostSqls = WriterUtil.renderPreOrPostSqls(
                postSqls, table);
        if (null != renderedPostSqls && !renderedPostSqls.isEmpty()) {

            LOG.info("Begin to preCheck postSqls:[{}].",
                    StringUtils.join(renderedPostSqls, ";"));
            for (String sql : renderedPostSqls) {
                try {
                    DBUtil.sqlValid(sql, type);
                } catch (ParserException e) {
                    throw RdbmsException.asPostSQLParserException(type, e, sql);
                }

            }
        }
    }


}
