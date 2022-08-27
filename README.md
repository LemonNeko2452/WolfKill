# 狼人杀
基于[NGA狼人杀](https://ngabbs.com/read.php?tid=11000590&rand=473)进行的mirai插件开发

项目地址 https://github.com/LemonNeko2452/WolfKill
## 使用说明
本插件开发环境为2.11.1，请尽量使用最新本console
### 相关文档
* [Console Terminal](https://docs.mirai.mamoe.net/ConsoleTerminal.html)
* [chat-command](https://github.com/project-mirai/chat-command)
### 前置插件
在使用本插件前，需要安装[chat-command](https://github.com/project-mirai/chat-command/releases)插件

目的是让指令能够在聊天环境下执行
### 启动前
使用本插件前，需要将[图片压缩包](https://github.com/LemonNeko2452/WolfKill/blob/master/img.zip)解压，然后将其中的img文件夹放入mcl根目录下的data文件夹内，
文件夹内的图片可以更改，但是要保证文件名不变

### 加载
将在[Releases](https://github.com/LemonNeko2452/WolfKill/releases/tag/0.1.0)中下载的jar文件直接放入mcl根目录的plugins文件夹 

### 第一次启动后
由于本插件大量使用指令来完成交互，因此必须给所有人使用命令的权限

将以下命令在console中粘贴即可
```shell
/permission permit * work.anqi.wolfkill:*
```

## 下载
[Releases](https://github.com/LemonNeko2452/WolfKill/releases/tag/0.1.0)

## 命令别名

在 mcl根目录/config/work.anqi.WolfKill 目录下的Command.yml文件中可以设置本插件所有命令的别名，注意不要将别名设置重复

## 游戏流程
### 创建游戏
![img.png](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img.png)
每个群里只能存在一场游戏，标准模式为8人场
![img_1](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img_1.png)
![img_2](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img_2.png)
自定义模式可以有多位同类型神民，根据提示输入6个数字，中间用空格或逗号隔开
### 加入游戏
创建者自动加入游戏
![img_3](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img_3.png)
![img_4](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img_4.png)
### 开始游戏
![img_6](https://lemon-neko.oss-cn-beijing.aliyuncs.com/img/img_6.png)
