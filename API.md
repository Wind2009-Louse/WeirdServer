
## 【管理端】全卡片搜索

#### 接口URL
> 127.0.0.1:15163/weird_project/card/list/admin?name=admin&password=1&package=&card=义豪&rare=&page=1

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| package     | - | 必填 | 卡包名，模糊匹配 |
| card     | 义豪 | 必填 | 卡片名，模糊匹配 |
| rare     | - | 必填 | 稀有度，精确匹配 |
| page     | 1 | 必填 | 页码 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"cardName": "义豪灵蜥",
				"packageName": "再造的世界",
				"rare": "N"
			},
			{
				"cardName": "巨歧蜥·魔蜥义豪",
				"packageName": "再造的世界",
				"rare": "N"
			},
			{
				"cardName": "歧蜥·魔蜥义豪",
				"packageName": "再造的世界",
				"rare": "N"
			},
			{
				"cardName": "魔蜥义豪",
				"packageName": "再造的世界",
				"rare": "N"
			}
		],
		"pageSize": 20,
		"totalCount": 4,
		"totalPage": 1
	}
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \"\""
}
```


## 【管理端】添加卡片信息

#### 接口URL
> 127.0.0.1:15163/weird_project/card/add?name=admin&password=1&package=霸王的威压&card=入魔鬼·血石&rare=N

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| package     | 霸王的威压 | 必填 | 卡包名 |
| card     | 入魔鬼·血石 | 必填 | 卡片名 |
| rare     | N | 必填 | 稀有度 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "添加成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "卡片[入魔鬼·血石]已存在！"
}
```


## 【管理端】修改卡片名称

#### 接口URL
> 127.0.0.1:15163/weird_project/card/update?name=admin&password=1&package=再造的世界&oldname=草兽&newname=除草兽

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| package     | 再造的世界 | 必填 | 卡包名 |
| oldname     | 草兽 | 必填 | 旧卡名 |
| newname     | 除草兽 | 必填 | 新卡名 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "卡片[除草兽]不存在！"
}
```


## 【ALL】全卡片拥有情况搜索

#### 接口URL
> 127.0.0.1:15163/weird_project/card/list?package=&card=&rare=&target=&page=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| package     | - | 选填 | 卡包名，模糊搜索 |
| card     | - | 选填 | 卡片名，模糊搜索 |
| rare     | - | 选填 | 稀有度，精确搜索 |
| target     | - | 选填 | 用户名，模糊搜索 |
| page     | - | 选填 | 页码 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"cardName": "一骑加势",
				"count": 0,
				"packageName": "再造的世界",
				"rare": "N",
				"userName": "test"
			},
			{
				"cardName": "义豪灵蜥",
				"count": 0,
				"packageName": "再造的世界",
				"rare": "N",
				"userName": "test"
			},
			{
				"cardName": "除草兽",
				"count": 3,
				"packageName": "再造的世界",
				"rare": "N",
				"userName": "test"
			}
		],
		"pageSize": 20,
		"totalCount": 3,
		"totalPage": 1
	}
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "Required int parameter 'page' is not present"
}
```


## 【管理端】新增卡包

#### 接口URL
> 127.0.0.1:15163/weird_project/package/add?name=admin&password=1&package=咕咕咕

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| package     | 咕咕咕 | 必填 | 卡包名 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "新增成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "卡包[霸王的威压]已存在！"
}
```


## 【管理端】修改卡包名

#### 接口URL
> 127.0.0.1:15163/weird_project/package/update?name=admin&password=1&oldname=咕咕咕&newname=咕咕咕咕

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| oldname     | 咕咕咕 | 必填 | 旧卡包名 |
| newname     | 咕咕咕咕 | 必填 | 新卡包名 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "找不到该卡包：[咕咕咕]！"
}
```


## 【管理端】添加新用户
创建的用户密码默认为``E10ADC3949BA59ABBE56E057F20F883E``（``123456``经过32位大写MD5加密后）
#### 接口URL
> 127.0.0.1:15163/weird_project/user/add?name=admin&password=1&target=新的用户

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| target     | 新的用户 | 必填 | 新用户名 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "添加成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "该用户已存在！"
}
```


## 【管理端】修改用户持有的卡片数量

#### 接口URL
> 127.0.0.1:15163/weird_project/user/card/update?name=admin&password=1&package=再造的世界&card=除草兽&target=test&count=2

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| package     | 再造的世界 | 必填 | 卡包名 |
| card     | 除草兽 | 必填 | 卡片名 |
| target     | test | 必填 | 用户名 |
| count     | 2 | 必填 | 修改后的数量 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "[test]的卡片[除草兽]的数量没有变化！"
}
```


## 【管理端】修改用户的尘数

#### 接口URL
> 127.0.0.1:15163/weird_project/user/dust?name=admin&password=1&target=&count=1

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| target     | - | 必填 | 对象用户名 |
| count     | 1 | 必填 | 修改后的尘数量 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "用户名为空！"
}
```


## 【管理端】修改用户月见黑计数

#### 接口URL
> 127.0.0.1:15163/weird_project/user/award?name=admin&password=1&target=新的用户&award=1

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| target     | 新的用户 | 必填 | 对象用户名 |
| award     | 1 | 必填 | 修改后的月见黑数量 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "用户名为空！"
}
```


## 【ALL】检查用户类型
返回``ADMIN``、``NORMAL``、``UNLOGIN``三种。
服务器不会有任何更新动作。
#### 接口URL
> 127.0.0.1:15163/weird_project/user/check?name=&password=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | - | 选填 | 用户名 |
| password     | - | 选填 | 用户密码 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "ADMIN"
}
```


#### 错误响应示例
```javascript
{
	"code": 200,
	"data": "UNLOGIN"
}
```


## 【ALL】查询用户信息
``duelPoint``为DP，在该版尚未用到，属于预留变量
#### 接口URL
> 127.0.0.1:15163/weird_project/user/list?user=&page=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| user     | - | 选填 | 用户名，模糊搜索 |
| page     | - | 选填 | 页码 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"duelPoint": 0,
				"dustCount": 0,
				"nonawardCount": 0,
				"userId": 8,
				"userName": "新用户"
			},
			{
				"duelPoint": 0,
				"dustCount": 1,
				"nonawardCount": 1,
				"userId": 9,
				"userName": "新的用户"
			}
		],
		"pageSize": 20,
		"totalCount": 2,
		"totalPage": 1
	}
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \"\""
}
```


## 【玩家端】直接将尘转换为卡片

#### 接口URL
> 127.0.0.1:15163/weird_project/user/card/change?name=新的用户&password=123456&card=草兽

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | 新的用户 | 必填 | 操作用户名称 |
| password     | 123456 | 必填 | 操作用户密码 |
| card     | 草兽 | 必填 | 卡片名 |







#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "转换失败！"
}
```


## 【管理端】设置某个抽卡结果是否适用

#### 接口URL
> 127.0.0.1:15163/weird_project/roll/set?name=admin&password=1&id=3&status=0

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| id     | 3 | 必填 | 抽卡结果id |
| status     | 0 | 必填 | 要设置的状态（0=使用，1=禁用） |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "抽卡记录状态没有被修改！"
}
```


## 【ALL】查询抽卡结果

#### 接口URL
> 127.0.0.1:15163/weird_project/roll/list?package=&user=&page=1

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| package     | - | 选填 | 卡包名，模糊搜索 |
| user     | - | 选填 | 用户名，模糊搜索 |
| page     | 1 | 选填 | 页码 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"isDisabled": 0,
				"rollId": 4,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "怨念之魂 业火",
						"rare": "N"
					},
					{
						"cardName": "喔喔雏鸡",
						"rare": "N"
					},
					{
						"cardName": "增草剂",
						"rare": "R"
					}
				],
				"rollUserName": "新的用户",
				"time": "2020-09-06 13:27:19"
			},
			{
				"isDisabled": 0,
				"rollId": 3,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "除草兽",
						"rare": "N"
					},
					{
						"cardName": "义豪灵蜥",
						"rare": "N"
					},
					{
						"cardName": "一骑加势",
						"rare": "N"
					}
				],
				"rollUserName": "test",
				"time": "2020-09-05 17:48:00"
			},
			{
				"isDisabled": 0,
				"rollId": 2,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "除草兽",
						"rare": "N"
					},
					{
						"cardName": "义豪灵蜥",
						"rare": "N"
					},
					{
						"cardName": "一骑加势",
						"rare": "N"
					}
				],
				"rollUserName": "test",
				"time": "2020-09-05 16:55:38"
			},
			{
				"isDisabled": 0,
				"rollId": 1,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "除草兽",
						"rare": "N"
					},
					{
						"cardName": "义豪灵蜥",
						"rare": "N"
					},
					{
						"cardName": "一骑加势",
						"rare": "N"
					}
				],
				"rollUserName": "test",
				"time": "2020-09-05 16:53:11"
			}
		],
		"pageSize": 20,
		"totalCount": 4,
		"totalPage": 1
	}
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \"\""
}
```


## 【RMXP端】发送抽卡信息

#### 接口URL
> 127.0.0.1:15163/weird_project/weirdUI/roll?name=admin&password=1&target=新的用户&package=再造的世界&card1=怨念之魂 业火&card2=喔喔雏鸡&card3=增草剂

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| target     | 新的用户 | 必填 | 对象用户名 |
| package     | 再造的世界 | 必填 | 卡包名 |
| card1     | 怨念之魂 业火 | 必填 | 卡片名1 |
| card2     | 喔喔雏鸡 | 必填 | 卡片名2 |
| card3     | 增草剂 | 必填 | 卡片名3 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "记录成功!"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "权限不足！"
}
```


## 【ALL】HOME
测试项目是否正常启动的接口
#### 接口URL
> 127.0.0.1:15163/weird_project/

#### 请求方式
> GET

#### Content-Type
> form-data







#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "Hello!\n你好！\nこんにちは"
}
```


