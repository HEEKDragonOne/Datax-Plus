# DataX

DataX 是阿里云 DataWorks数据集成 的开源版本，在阿里巴巴集团内被广泛使用的离线数据同步工具/平台。DataX 实现了包括
MySQL、Oracle、OceanBase、SqlServer、Postgre、HDFS、Hive、ADS、HBase、TableStore(OTS)、MaxCompute(ODPS)、Hologres、DRDS, databend
等各种异构数据源之间高效的数据同步功能。

# Features

DataX本身作为数据同步框架，将不同数据源的同步抽象为从源头数据源读取数据的Reader插件，以及向目标端写入数据的Writer插件，理论上DataX框架可以支持任意数据源类型的数据同步工作。同时DataX插件体系作为一套生态系统,
每接入一套新数据源该新加入的数据源即可实现和现有的数据源互通。

# 新扩展

|    数据源     | Reader(读) |           Writer(写)           |
|:----------:|:---------:|:-----------------------------:|
|     达梦     |     √     |               √               |
| Postgresql |           | 增加writeMode支持`update(主键或唯一键)`模式 |

# Support Data Channels

DataX目前已经有了比较全面的插件体系，主流的RDBMS数据库、NOSQL、大数据计算系统都已经接入，目前支持数据如下图：

| 类型           | 数据源                       | Reader(读) | Writer(写) |                                                                                                          文档                                                                                                          |
|--------------|---------------------------|:---------:|:---------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| RDBMS 关系型数据库 | MySQL                     |     √     |     √     |               [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/mysqlreader/doc/mysqlreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/mysqlwriter/doc/mysqlwriter.md)               |
|              | Oracle                    |     √     |     √     |             [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/oraclereader/doc/oraclereader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/oraclewriter/doc/oraclewriter.md)             |
|              | OceanBase                 |     √     |     √     | [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/oceanbasev10reader/doc/oceanbasev10reader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/oceanbasev10writer/doc/oceanbasev10writer.md) |
|              | SQLServer                 |     √     |     √     |       [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/sqlserverreader/doc/sqlserverreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/sqlserverwriter/doc/sqlserverwriter.md)       |
|              | PostgreSQL                |     √     |     √     |     [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/postgresqlreader/doc/postgresqlreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/postgresqlwriter/doc/postgresqlwriter.md)     |
|              | DRDS                      |     √     |     √     |                 [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/drdsreader/doc/drdsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/drdswriter/doc/drdswriter.md)                 |
|              | Kingbase                  |     √     |     √     |                 [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/drdsreader/doc/drdsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/drdswriter/doc/drdswriter.md)                 |
|              | 通用RDBMS(支持所有关系型数据库)       |     √     |     √     |               [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/rdbmsreader/doc/rdbmsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/rdbmswriter/doc/rdbmswriter.md)               |
| 阿里云数仓数据存储    | ODPS                      |     √     |     √     |                 [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/odpsreader/doc/odpsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/odpswriter/doc/odpswriter.md)                 |
|              | ADB                       |           |     √     |                                                          [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/adbmysqlwriter/doc/adbmysqlwriter.md)                                                           |
|              | ADS                       |           |     √     |                                                               [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/adswriter/doc/adswriter.md)                                                                |
|              | OSS                       |     √     |     √     |                   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/ossreader/doc/ossreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/osswriter/doc/osswriter.md)                   |
|              | OCS                       |           |     √     |                                                               [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/ocswriter/doc/ocswriter.md)                                                                |
|              | Hologres                  |           |     √     |                                                      [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hologresjdbcwriter/doc/hologresjdbcwriter.md)                                                       |
|              | AnalyticDB For PostgreSQL |           |     √     |                                                                                                          写                                                                                                           |
| 阿里云中间件       | datahub                   |     √     |     √     |                                                                                                         读 、写                                                                                                         |
|              | SLS                       |     √     |     √     |                                                                                                         读 、写                                                                                                         |
| 图数据库         | 阿里云 GDB                   |     √     |     √     |                   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/gdbreader/doc/gdbreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/gdbwriter/doc/gdbwriter.md)                   |
|              | Neo4j                     |           |     √     |                                                             [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/neo4jwriter/doc/neo4jwriter.md)                                                              |
| NoSQL数据存储    | OTS                       |     √     |     √     |                   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/otsreader/doc/otsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/otswriter/doc/otswriter.md)                   |
|              | Hbase0.94                 |     √     |     √     |       [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase094xreader/doc/hbase094xreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase094xwriter/doc/hbase094xwriter.md)       |
|              | Hbase1.1                  |     √     |     √     |         [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase11xreader/doc/hbase11xreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase11xwriter/doc/hbase11xwriter.md)         |
|              | Phoenix4.x                |     √     |     √     |   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase11xsqlreader/doc/hbase11xsqlreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase11xsqlwriter/doc/hbase11xsqlwriter.md)   |
|              | Phoenix5.x                |     √     |     √     |   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase20xsqlreader/doc/hbase20xsqlreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hbase20xsqlwriter/doc/hbase20xsqlwriter.md)   |
|              | MongoDB                   |     √     |     √     |           [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/mongodbreader/doc/mongodbreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/mongodbwriter/doc/mongodbwriter.md)           |
|              | Cassandra                 |     √     |     √     |       [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/cassandrareader/doc/cassandrareader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/cassandrawriter/doc/cassandrawriter.md)       |
| 数仓数据存储       | StarRocks                 |     √     |     √     |                                                        读 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/starrockswriter/doc/starrockswriter.md)                                                        |
|              | ApacheDoris               |           |     √     |                                                             [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/doriswriter/doc/doriswriter.md)                                                              |
|              | ClickHouse                |     √     |     √     |     [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/clickhousereader/doc/clickhousereader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/clickhousewriter/doc/clickhousewriter.md)     |
|              | Databend                  |           |     √     |                                                          [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/databendwriter/doc/databendwriter.md)                                                           |
|              | Hive                      |     √     |     √     |                 [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hdfsreader/doc/hdfsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hdfswriter/doc/hdfswriter.md)                 |
|              | kudu                      |           |     √     |                                                              [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hdfswriter/doc/hdfswriter.md)                                                               |
|              | selectdb                  |           |     √     |                                                          [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/selectdbwriter/doc/selectdbwriter.md)                                                           |
| 无结构化数据存储     | TxtFile                   |     √     |     √     |           [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/txtfilereader/doc/txtfilereader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/txtfilewriter/doc/txtfilewriter.md)           |
|              | FTP                       |     √     |     √     |                   [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/ftpreader/doc/ftpreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/ftpwriter/doc/ftpwriter.md)                   |
|              | HDFS                      |     √     |     √     |                 [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hdfsreader/doc/hdfsreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/hdfswriter/doc/hdfswriter.md)                 |
|              | Elasticsearch             |           |     √     |                                                     [写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/elasticsearchwriter/doc/elasticsearchwriter.md)                                                      |
| 时间序列数据库      | OpenTSDB                  |     √     |           |                                                          [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/opentsdbreader/doc/opentsdbreader.md)                                                           |
|              | TSDB                      |     √     |     √     |               [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/tsdbreader/doc/tsdbreader.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/tsdbwriter/doc/tsdbhttpwriter.md)               |
|              | TDengine                  |     √     |     √     |      [读](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/tdenginereader/doc/tdenginereader-CN.md) 、[写](https://github.com/HEEKDragonOne/Datax-Plus/blob/master/tdenginewriter/doc/tdenginewriter-CN.md)      |



