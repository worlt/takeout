# 游客用例图

## Guest Use Case Diagram

```mermaid
flowchart LR
    Guest(("👤 游客"))

    subgraph 游客功能
        UC1["浏览博客首页"]
        UC2["查看博客详情"]
        UC3["搜索博客"]
        UC4["按分类浏览"]
        UC5["按标签浏览"]
        UC6["查看热门博客"]
        UC7["查看推荐博客"]
        UC8["用户注册"]
        UC9["用户登录"]
        UC10["邮箱验证"]
        UC11["图形验证码"]
        UC12["找回密码"]
    end

    Guest --> UC1
    Guest --> UC2
    Guest --> UC3
    Guest --> UC4
    Guest --> UC5
    Guest --> UC6
    Guest --> UC7
    Guest --> UC8
    Guest --> UC9
    Guest --> UC12

    UC8 -.->"include" UC10
    UC8 -.->"include" UC11
    UC9 -.->"include" UC11
    UC12 -.->"include" UC10
    UC3 -.->"include" UC2
```
