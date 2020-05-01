## ManageSystem-be

前端仓库： [ManageSystem-fe](https://github.com/LinLorry/ManageSystem-fe)

### 后端开发环境
 spring boot
 
### 部署方法

配置环境变量：
- DATABASE_URL为jdbc数据库url
- DATABASE_USER为数据库用户账号
- DATABASE_PWD为数据库用户密码

或直接修改[application.yaml](src/main/resources/application.yaml)内的数据库配置：
```yaml
...
  datasource:
    url: ${DATABASE_URL}        // jdbc数据库url
    username: ${DATABASE_USER}  // 数据库用户账号
    password: ${DATABASE_PWD}   // 数据库用户秘密
...
```

修改完成后，使用命令行执行部署

```shell script
mvn spring-boot:run
```
