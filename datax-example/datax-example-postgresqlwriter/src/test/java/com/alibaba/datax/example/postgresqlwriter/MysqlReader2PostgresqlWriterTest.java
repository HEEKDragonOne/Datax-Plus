package com.alibaba.datax.example.postgresqlwriter;

import com.alibaba.datax.example.ExampleContainer;
import com.alibaba.datax.example.util.PathUtil;
import org.junit.Test;

/**
 * {@code Author} FuYouJ
 * {@code Date} 2023/8/14 20:16
 */

public class MysqlReader2PostgresqlWriterTest {
    @Test
    public void test() {
        String path = "/mysql2postgresql.json";
        String jobPath = PathUtil.getAbsolutePathFromClassPath(path);
        ExampleContainer.start(jobPath);
    }
}
