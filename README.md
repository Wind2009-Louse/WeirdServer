# 云诡异空间

## 简介

给诡异空间提供个平台，把一些离散到每个人上的功能（卡片记录、卡池查询等）整在一起。

本项目为后端，接口见[API文档](API.md)，前端项目见https://github.com/DarkNin/WeirdProject。

## 运行要求

* JAVA 1.8+
* Maven
* ~~MySQL 5.1+~~
* 开放访问的主机

### 备注

本项目使用sqlite作为数据库，编译运行后需要将sqlite数据库（``data.db``）放在和jar文件的同一目录下。

## 编译运行方式

### 编译

```Shell
mvn clean package spring-boot:repackage
```

### 运行

```Shell
cd target
java -jar weird-1.0-RELEASE.jar
```

## 给管理端

管理帐号默认密码为``123456``(32位小写MD5加密前)，为避免权限滥用，请在云诡异可用后尽快修改密码。