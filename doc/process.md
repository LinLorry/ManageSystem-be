## Process Api

- [Create Api:](#create-api)
- [Update Api:](#update-api)
- [Find Api:](#find-api)

### Create Api:

`URL: /api/process/create`

Authentication: [ `User`, `Admin` ]
request:
- method: `POST`

  body:
  ```json
  {
    "name": "name",
    "comment": "comment"
  }
  ```

  response:
  - create success:
    ```json
    {
      "status": 1,
      "message": "Create process success.",
      "data": {
        "id": int,
        "name": "name",
        "comment": "comment",
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

`URL: /api/process/update`

Authentication: [ `User`, `Admin` ]
request:
- method: `POST`
  
  body:
  ```json
  {
    "name": "name",
    "comment": "comment"
  }
  ```
  
  response:
  - update success:
    ```json
    {
      "status": 1,
      "message": "Update process success.",
      "data": {
        "id": int,
        "name": "name",
        "comment": "comment",
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

`URL: /api/process/find`

Authentication: [ `User`, `Admin` ]
- method: `GET`
  
  param:
  - id: int
  - name: str
  - comment: str
  - pageNumber: int

  response:
  - find success:
    ```json
    {
      "status": 1,
      "message": "Get process success.",
      "data": [
        {
            "id": int,
            "name": "name",
            "comment": "comment",
            "createTime": "create time: Timestamp",
            "updateTime": "update time: Timestamp"
        },
        ...
      ]
    }
    ```