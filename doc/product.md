## Product Api

- [Create Api](#create-api)
- [Update Api](#update-api)
- [Find Api](#find-api)
- [Finish Api](#finish-api)
- [Create Time At Today Api](#create-time-at-today-api)
- [According End Time Api](#according-end-time-api)

### Create Api:

创建订单接口

`URL: /api/product/create`

Authentication: [ `User`, `Admin` ]
request:
- method: `POST`

  body:
  ```json
  {
    "serial": "*订单号，product serial: String",
    "workId": "*订单生产流程Id，product product id: Integer",
    "endTime": "*订单截至时间，product end time: Timestamp",
  }
  ```

  response:
  - create success:
    ```json
    {
      "status": 1,
      "message": "Create product success.",
      "data": {
        "id": int,
        "serial": "product serial: String",
        "workId": int,
        "workName": "product product name: String",
        "status": "product status: String",
        "createUser": "create user name: String",
        "createTime": "product create time: Timestamp",
        "endTime": "product end time: Timestamp"
      }
    }
    ```
  - create failed:
    ```json
    {
      "status": 0,
      "message": "message"
    }
    ```

### Update Api:

更新订单接口

`URL: /api/product/update`

Authentication: [ `Admin` ]
request:
- method: `POST`
  
  body:
  ```json
  {
    "id": *订单Id: int
    "serial": "订单号，product serial: String[must]",
    "workId": "订单生产流程Id，product product id: Integer[must]",
    "endTime": "订单截至时间，product end time: Timestamp"
  }
  ```
  
  response:
  - update success:
    ```json
    {
      "status": 1,
      "message": "Update product success.",
      "data": {
        "id": int,
        "serial": "product serial: String",
        "workId": int,
        "workName": "product product name: String",
        "status": "product status: String",
        "createUser": "create user name: String",
        "createTime": "product create time: Timestamp",
        "endTime": "product end time: Timestamp"
      }
    }
    ```
  - update failed:
    ```json
    {
      "status": 0,
      "message": "message"
    }
    ```

### Find Api:

`URL: /api/product/find`

查找订单接口

Authentication: [ `User`, `Admin` ]
- method: `GET`
  
  param:
  - id: int 订单Id
  - serial: str 订单号
  - status: [`progress`, `finish`] 订单状态
  - pageNumber: int 页号

  response:
  - find success:
    ```json
    {
      "status": 1,
      "message": "Get product success.",
      "data": {
        "total": 总页数: int,
        "products": [
          {
            "id": 订单Id: int,
            "serial": "product serial: String",
            "workId": int,
            "workName": "product product name: String",
            "status": "product status: String",
            "createUser": "create user name: String",
            "createTime": "product create time: Timestamp",
            "endTime": "product end time: Timestamp"
          },
          ...
        ]                 
      } 
    }
      } 
    }
    ```

### Finish Api

完成订单接口

`URL: /api/product/finish`

Authentication: [ `User`, `Admin` ]
- method: `POST`

  param:
  - id: *订单Id: int

  response:
  - finish success:
    ```json
    {
      "status": 1,
      "message": "Finish product success."
    }
    ```
  - error happen:
   ```json
   {
     "status": 0,
     "message": "message"
   }
   ```

### Create Time At Today Api

查询今天创建的订单接口

`URL: /api/product/todayCreate`

Authentication: [ `User`, `Admin` ]
- method: `GET`

  param:
  - pageNumber: 页号: int

  response:
  - get success:
    ```json
    {
      "status": 1,
      "message": "Get today create product success.",
      "data": {
        "total": int,
        "products": [
          {
            "id": int,
            "serial": "product serial: String",
            "workId": int,
            "workName": "product product name: String",
            "status": "product status: String",
            "createUser": "create user name: String",
            "createTime": "product create time: Timestamp",
            "endTime": "product end time: Timestamp"
          },
          ...
        ]                      
      } 
    }
    ```

### According End Time Api

根据截至时间查询接口

`URL: /api/product/accordEnd`

Authentication: [ `User`, `Admin` ]
- method: `GET`

  param:
  - accord: 截至时间距离当日的天数[0,1,2]: int
  - pageNumber: 页号: int

  response:
  - get success:
    ```json
    {
      "status": 1,
      "message": "Get products success.",
      "data": {
        "total": int,
        "products": [
          {
            "id": int,
            "serial": "product serial: String",
            "workId": int,
            "workName": "product product name: String",
            "status": "product status: String",
            "createUser": "create user name: String",
            "createTime": "product create time: Timestamp",
            "endTime": "product end time: Timestamp"
          },
          ...
        ]                      
      } 
    }
    ``` 
