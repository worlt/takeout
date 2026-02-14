# 管理员用例图

## Administrator Use Case Diagram

```mermaid
flowchart LR
    Admin(("👤 管理员"))

    subgraph 管理员功能
        UC0["管理员登录"]

        subgraph 用户管理
            UC1["查看用户列表"]
            UC2["封禁用户"]
            UC3["解封用户"]
            UC4["管理审核员"]
            UC4_1["新增审核员"]
            UC4_2["编辑审核员"]
        end

        subgraph 内容管理
            UC5["管理博客"]
            UC6["删除博客"]
            UC7["置顶博客"]
            UC8["推荐博客"]
            UC9["管理评论"]
            UC10["删除评论"]
            UC11["管理建议"]
        end

        subgraph 分类标签管理
            UC12["管理分类"]
            UC13["管理标签"]
        end

        subgraph 数据统计
            UC14["查看仪表盘"]
            UC15["访问量统计"]
            UC16["用户活跃度分析"]
            UC17["博客数据统计"]
        end

        subgraph 系统管理
            UC18["审核记录查看"]
            UC19["消息管理"]
            UC20["个人信息管理"]
        end
    end

    Admin --> UC0
    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC9
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC18
    Admin --> UC19
    Admin --> UC20

    UC4 -.->"extend" UC4_1
    UC4 -.->"extend" UC4_2
    UC5 -.->"extend" UC6
    UC5 -.->"extend" UC7
    UC5 -.->"extend" UC8
    UC9 -.->"extend" UC10
    UC14 -.->"include" UC15
    UC14 -.->"include" UC16
    UC14 -.->"include" UC17
    UC1 -.->"extend" UC2
    UC1 -.->"extend" UC3
```
