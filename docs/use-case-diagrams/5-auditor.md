# 审核员用例图

## Auditor Use Case Diagram

```mermaid
flowchart LR
    Auditor(("👤 审核员"))

    subgraph 审核员功能
        UC0["审核员登录"]

        subgraph 内容审核
            UC1["查看待审博客"]
            UC2["审核通过"]
            UC3["审核拒绝"]
            UC4["要求整改"]
            UC5["查看审核记录"]
            UC6["填写审核意见"]
        end

        subgraph 举报处理
            UC7["处理博客举报"]
            UC8["处理评论举报"]
            UC9["处理用户举报"]
            UC10["封禁用户"]
            UC11["封禁博客"]
            UC12["删除评论"]
        end

        subgraph 申诉处理
            UC13["查看用户申诉"]
            UC14["通过申诉"]
            UC15["拒绝申诉"]
        end

        UC16["个人信息管理"]
    end

    Auditor --> UC0
    Auditor --> UC1
    Auditor --> UC5
    Auditor --> UC7
    Auditor --> UC8
    Auditor --> UC9
    Auditor --> UC13
    Auditor --> UC16

    UC1 -.->"extend" UC2
    UC1 -.->"extend" UC3
    UC1 -.->"extend" UC4
    UC3 -.->"include" UC6
    UC4 -.->"include" UC6
    UC7 -.->"extend" UC11
    UC7 -.->"extend" UC10
    UC8 -.->"extend" UC12
    UC9 -.->"extend" UC10
    UC13 -.->"extend" UC14
    UC13 -.->"extend" UC15
```
