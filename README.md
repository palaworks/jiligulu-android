# <img src="./doc/img/jiligulu.svg" width="50px"> jiligulu for android

[![Build & Release](https://github.com/Thaumy/jiligulu-android/actions/workflows/build_and_release.yml/badge.svg)](https://github.com/Thaumy/jiligulu-android/actions/workflows/build_and_release.yml)

叽哩咕噜安卓客户端

<img src="./doc/img/appshot.png" width="320px">

叽哩咕噜是一款为噼哩啪啦设计的后台，支持：

* 离线管理文章评论
* 数据对比、同步和冲突解决
* 在多个用户间进行迁移

## 安装要求

### 安卓版本

Android 12 (API level 31) 或更高

### pilipala 服务器配置

需要安装 [GrpcApi](https://github.com/Thaumy/pilipala-plugin) 插件

## Build

```shell
git clone --depth 1 https://github.com/Thaumy/jiligulu-android.git
cd jiligulu-android
./protoc.sh # compile proto files
./gradlew build
```

You can also get binaries from [CI](https://github.com/Thaumy/jiligulu-android/actions) artifacts.
