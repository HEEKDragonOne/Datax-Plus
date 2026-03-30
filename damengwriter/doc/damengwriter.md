# DataX DaMengWriter

---

### 配置样例

```json
{
  "job": {
    "setting": {
      "speed": {
        "channel": 2
      }
    },
    "content": [
      {
        "reader": {
          "name": "mysqlreader",
          "parameter": {
            "username": "guest",
            "password": "guest123",
            "connection": [
              {
                "querySql": [
                  "select * from test.banner_info;"
                ],
                "jdbcUrl": [
                  "jdbc:mysql://192.168.199.13:23306/test"
                ]
              }
            ]
          }
        },
        "writer": {
          "name": "damengwriter",
          "parameter": {
            "writeMode": "update(id)",
            "username": "SYSDBA",
            "password": "r00t12345",
            "column": [
              "*"
            ],
            "connection": [
              {
                "jdbcUrl": "jdbc:dm://192.168.199.15:5236/test",
                "table": [
                  "test.banner_info"
                ]
              }
            ]
          }
        }
      }
    ]
  }
}

```

### 参数说明

* **writeMode**

    * 描述：控制写入数据到目标表采用 `insert into` 或者 `ON CONFLICT` 语句<br />

    * 必选：是 <br />

    * 所有选项：insert/update <br />

