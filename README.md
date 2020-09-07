# 云诡异空间

## 简介

给诡异空间提供个平台，把一些离散到每个人上的功能（卡片记录、卡池查询等）整在一起。

本项目为后端，接口见[API文档](API.md)。

## 运行要求

* JAVA 1.8+
* Maven
* MySQL 5.1+
* 开放访问的主机

### 备注

需要准备数据库（dump文件[在这里](src/main/resources/db)）。准备了mysql 5+和8+的两份，不过8.0大概也可以直接导5+的版本。

数据库的访问用户名和密码如果和默认的（``name``=``admin``, ``password``=``admin``）不一致，需要修改[jdbc.properties](src/main/resources/jdbc.properties)的对应配置。

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

管理帐号默认密码为``123456``(32位大写MD5加密前)，为避免权限滥用，请在云诡异可用后尽快修改密码。