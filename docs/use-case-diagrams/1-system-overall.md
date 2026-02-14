# 博客系统总体用例图

## System Overall Use Case Diagram

```mermaid
flowchart LR
    Guest(("👤 游客"))
    User(("👤 普通用户"))
    Auditor(("👤 审核员"))
    Admin(("👤 管理员"))

    subgraph 个人博客系统
        UC1["浏览博客"]
        UC2["搜索博客"]
        UC3["注册/登录"]
        UC4["博客管理"]
        UC5["互动交流"]
        UC6["社交功能"]
        UC7["私信通信"]
        UC8["内容举报"]
        UC9["用户申诉"]
        UC10["AI智能服务"]
        UC11["个人中心"]
        UC12["内容审核"]
        UC13["举报处理"]
        UC14["申诉处理"]
        UC15["用户管理"]
        UC16["内容管理"]
        UC17["分类标签管理"]
        UC18["数据统计"]
        UC19["系统管理"]
    end

    Guest --> UC1
    Guest --> UC2
    Guest --> UC3

    User -.->"继承" Guest
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9
    User --> UC10
    User --> UC11

    Auditor --> UC12
    Auditor --> UC13
    Auditor --> UC14

    Admin -.->"继承" Auditor
    Admin --> UC15
    Admin --> UC16
    Admin --> UC17
    Admin --> UC18
    Admin --> UC19
```
