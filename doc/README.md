# 生产管理系统接口文档

- [关于权限](#%e5%85%b3%e4%ba%8e%e6%9d%83%e9%99%90)
- [Admin](#admin)
- [User](#user)
- [Guest](#guest)
- [工序](#%e5%b7%a5%e5%ba%8f)
- [生产流程](#%e7%94%9f%e4%ba%a7%e6%b5%81%e7%a8%8b)
- [流程工序](#%e6%b5%81%e7%a8%8b%e5%b7%a5%e5%ba%8f)

## 关于权限

目前权限分为三级:
- Admin
- User
- Guest

访问权限为 Admin 和 User 的接口需要提供token

token的获取在[user](user.md)文档内

提供token的方式是在请求头上加上`Authorization`

内容是`${AUTHENTICATION_NAME:manage} token`

### Admin

Admin 拥有全局的管理权限，可以创建，访问，修改，删除一切元素

### User

User 拥有访问订单，工序，生产流程，流程工序的权限

### Guest

Guest 设计上仅有登陆权限，但目前为了测试方便，也有注册权限

## 工序

工序接口位于`/api/process`路经底下

详情请查看[工序文档](process.md)

## 生产流程

生产流程接口位于`/api/work`路经底下

详情请查看[生产流程文档](work.md)

## 流程工序

流程工序接口位于`/api/workProcess`路经底下

详情请查看[流程工序文档](work-process.md)