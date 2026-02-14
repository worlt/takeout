# 用户总用例图

## User Total Use Case Diagram

```mermaid
flowchart LR
    User(("👤 普通用户"))

    subgraph 用户功能总览
        subgraph 博客管理
            A1["创作博客"]
            A2["编辑博客"]
            A3["删除博客"]
            A4["保存草稿"]
            A5["发布博客"]
            A6["查看我的博客"]
        end

        subgraph 互动交流
            B1["发表评论"]
            B2["回复评论"]
            B3["点赞博客"]
            B4["收藏博客"]
            B5["分享博客"]
            B6["查看浏览历史"]
        end

        subgraph 社交通信
            C1["关注/取消关注"]
            C2["查看关注列表"]
            C3["查看粉丝列表"]
            C4["发送私信"]
            C5["查看消息通知"]
        end

        subgraph 举报与申诉
            D1["举报内容"]
            D2["提交申诉"]
            D3["提交建议"]
        end

        subgraph AI智能服务
            E1["AI智能对话"]
            E2["AI写作辅助"]
        end

        subgraph 个人中心
            F1["修改个人信息"]
            F2["修改头像"]
            F3["修改密码"]
        end
    end

    User --> A1
    User --> A2
    User --> A3
    User --> A5
    User --> A6
    User --> B1
    User --> B3
    User --> B4
    User --> B5
    User --> B6
    User --> C1
    User --> C4
    User --> C5
    User --> D1
    User --> D2
    User --> D3
    User --> E1
    User --> E2
    User --> F1

    A1 -.->"extend" A4
    A1 -.->"extend" A5
    B1 -.->"extend" B2
    C1 -.->"include" C2
    C1 -.->"include" C3
    F1 -.->"extend" F2
    F1 -.->"extend" F3
```
