## User Api

- [Registry Api](#registry-api)
- [Token Api](#token-api)
- [Profile Api](#profile-api)
- [Jude Admin Api](#jude-admin-api)


### Registry Api:

`URL: /api/user/registry`

Authentication: [ `Guest` ]
request:
- method: `POST`
  
  body:
  ```json
  {
    "username": "username",
    "password": "password", 
    "name": "name"
  }
  ```
  response:
  - registry success:
    ```json
    {
      "status": 1,
      "message": "Registry Success",
      "data": {
        "id": int,
        "username": "username",
        "name": "name"
      }
    }
    ```
  - registry failed:
    ```json
    {
      "status": 0,
      "message": "message"
    }
    ```

### Token Api:

`URL: /api/user/token`

Authentication: [ `Guest` ]
- method: `POST`
  
  body:
  ```json
  {
    "username": "username",
    "password": "password"
  }
  ```
  response:
  - login success: 
    ```json
    {
      "status": 1,
      "message": "Login success",
      "data": "token"
    }
    ```
  - password wrong:
    ```json
    {
      "status": 0,
      "message": "Wrong password."
    }
    ```
  - user not exist:
    ```json
    {
      "status": 0,
      "message": "The user does not exist."
    }
    ```

### Profile Api:

`URL: /api/user/profile`

Authentication: [ `User`, `Admin` ]
- method: `GET`

  response:
  - success:
    ```json
    {
      "status": 1,
      "message": "Get profile success.",
      "data": {
        "id": int,
        "name": "name",
        "username": "username"
      }
    }
    ```
- method: `POST`
  
  body:
  ```json
  {
    "name": "name"
  }
  ```
  response:
  - success:
    ```json
    {
      "status": 1,
      "message": "Update profile success.",
      "data": {
        "id": int,
        "name": "name",
        "username": "username"
      }
    }
    ```

### Jude Admin Api:

 `URL: /api/user/isAdmin`
 
 Authentication: [ `User`, `Admin` ]
 - method: `GET`
 
   response:
   - user is admin:
     ```json
     {
       "status": 1,
       "message": "Get success.",
       "data": true
     }
     ```
   - user isn't admin:
     ```json
     {
       "status": 1,
       "message": "Get success.",
       "data": false
     }
     ```