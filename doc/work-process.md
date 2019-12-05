## Work Api

- [Create Api:](#create-api)
- [Update Api:](#update-api)
- [Find Api:](#find-api)

### Create Api:

`URL: /api/workProcess/create`

Authentication: [ `User`, `Admin` ]
request:
- method: `POST`

  body:
  ```json
  {
    "workId": int,
    "processId": int,
    "sequenceNumber": int
  }
  ```

  response:
  - create success:
    ```json
    {
      "status": 1,
      "message": "Add process in work success.",
      "data": {
        "workId": int,
        "workName": "the work name: String",
        "processId": int,
        "processName": "the process name: String",
        "sequenceNumber": int,
        "createTime": "create time: Timestamp",
        "updateTime": "update time: Timestamp"
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

`URL: /api/workProcess/update`

Authentication: [ `User`, `Admin` ]
request:
- method: `POST`
  
  body:
  ```json
  {
    "workId": int,
    "processId": int,
    "sequenceNumber": int
  }
  ```
  
  response:
  - update success:
    ```json
    {
      "status": 1,
      "message": "Update process in work success.",
      "data": {
        "workId": int,
        "workName": "the work name: String",
        "processId": int,
        "processName": "the process name: String",
        "sequenceNumber": int,
        "createTime": "create time: Timestamp",
        "updateTime": "update time: Timestamp"
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

`URL: /api/workProcess/find`

Authentication: [ `User`, `Admin` ]
- method: `GET`
  
  body:
  ```json
  {
    "sequenceNumber": int
    "createTimeBefore": "Timestamp",
    "updateTimeBefore": "work process's update time before this value: Timestamp",
    "createTimeAfter": "work process's create time after this value: Timestamp",
    "createTimeAfter": "work process's update time after this value: Timestamp",
    "work": {
      "id": int,
      "name": "work process's work name contains this value: String",
      "comment": "work process's work comment contains this value: String",
      "createTimeBefore": "work process's work create time before this value: Timestamp",
      "updateTimeBefore": "work process's work update time before this value: Timestamp",
      "createTimeAfter": "work process's work create time after this value: Timestamp",
      "createTimeAfter": "work process's work update time after this value: Timestamp"
    },
    "process": {
      "id": int,
      "name": "work process's process name contains this value: String",
      "comment": "work process's process comment contains this value: String",
      "createTimeBefore": "work process's process create time before this value: Timestamp",
      "updateTimeBefore": "work process's process update time before this value: Timestamp",
      "createTimeAfter": "work process's process create time after this value: Timestamp",
      "createTimeAfter": "work process's process update time after this value: Timestamp"
    }
  }
  ```

  response:
  - find success:
    ```json
    {
      "status": 1,
      "message": "Get work process success.",
      "data": [
        {
            "workId": int,
            "workName": "the work name: String",
            "processId": int,
            "processName": "the process name: String",
            "sequenceNumber": int,
            "createTime": "create time: Timestamp",
            "updateTime": "update time: Timestamp"
        },
        ...
      ]
    }
    ```