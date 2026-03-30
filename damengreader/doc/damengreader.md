# DaMengReader 插件文档

___

### 配置样例

* 配置一个从达梦数据库同步抽取数据到本地的作业:

```
{
    "job": {
        "setting": {
            "speed": {
                "channel": 2
            },
            "errorLimit": {
                "record": 0
            }
        },
        "content": [
            {
                "reader": {
                    "name": "damengreader",
					"parameter": {
                        "username": "SYSDBA",
                        "password": "r00t12345",
                        "column": [
                            "*"
                        ],
                        "connection": [
                            {
                                "jdbcUrl": ["jdbc:dm://192.168.199.15:5236/test"],
                                "table": [
                                    "test.banner_info"
                                ]
                            }
                        ]
                    }
                },
                "writer": {
                    "name": "streamwriter",
                    "parameter": {
                        "print": true,
                        "encoding": "UTF-8"
                    }
                }
            }
        ]
    }
}

```


