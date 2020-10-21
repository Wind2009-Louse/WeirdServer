
## 【管理端】全卡片搜索

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/list/admin

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
    "name": "admin",
    "password": "",
    "packageName": "",
    "cardName": "",
    "rareList": ["UR","SR"],
    "page": 1,
    "pageSize": 10
}
```

#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"cardName": "入魔邪龙 乌洛波洛斯",
				"desc": "[入魔邪龙 乌洛波洛斯]\n怪兽/效果/超量 ★4 暗/龙族 2750/1950\n4星怪兽×3\r\n1回合1次，可以把这张卡1个超量素材取除，从以下效果选择1个发动。以下效果只在这张卡在场上表侧表示存在各能选择1次。\r\n●选择对方场上存在的1张卡回到持有者手卡。\r\n●对方手卡随机选1张送去墓地。\r\n●选择对方墓地存在的1张卡从游戏中除外。",
				"packageName": "LEGEND",
				"picId": 38273745,
				"rare": "SR"
			},
			{
				"cardName": "冥府的使者 格斯",
				"desc": "",
				"packageName": "LEGEND",
				"picId": 0,
				"rare": "SR"
			},
			{
				"cardName": "冰结界之龙 光枪龙",
				"desc": "[冰结界之龙 光枪龙]\n怪兽/效果/同调 ★6 水/海龙族 2300/1400\n调整＋调整以外的怪兽1只以上\r\n这个卡名的效果1回合只能使用1次。\r\n①：把手卡任意数量丢弃去墓地，以丢弃数量的对方场上的卡为对象才能发动。那些卡回到持有者手卡。",
				"packageName": "LEGEND",
				"picId": 50321796,
				"rare": "SR"
			},
			{
				"cardName": "妖精传姬-白雪",
				"desc": "[妖精传姬-白雪]\n怪兽/效果 ★4 光/魔法师族 1850/1000\n①：这张卡召唤·特殊召唤成功的场合，以对方场上1只表侧表示怪兽为对象才能发动。那只怪兽变成里侧守备表示。\r\n②：这张卡在墓地存在的场合，从自己的手卡·场上·墓地把这张卡以外的7张卡除外才能发动。这张卡特殊召唤。这个效果在对方回合也能发动。",
				"packageName": "LEGEND",
				"picId": 55623480,
				"rare": "SR"
			},
			{
				"cardName": "幻影英雄 突袭魔女",
				"desc": "[幻影英雄 突袭魔女]\n怪兽/效果 ★8 暗/战士族 2700/1900\n这张卡表侧表示上级召唤的场合，可以作为怪兽的代替而把自己场上的陷阱卡解放。\r\n①：这张卡召唤成功时才能发动。对方场上的魔法·陷阱卡全部破坏。这个效果发动的回合，自己不是「英雄」怪兽不能特殊召唤。",
				"packageName": "LEGEND",
				"picId": 82697428,
				"rare": "SR"
			},
			{
				"cardName": "彼岸的旅人 但丁",
				"desc": "[彼岸的旅人 但丁]\n怪兽/效果/超量 ★3 光/战士族 1000/2500\n3星怪兽×2\r\n①：1回合1次，把这张卡1个超量素材取除，从自己卡组上面把最多3张卡送去墓地才能发动。这张卡的攻击力直到回合结束时上升因为这个效果发动而送去墓地的卡数量×500。\r\n②：这张卡攻击的场合，战斗阶段结束时变成守备表示。\r\n③：这张卡被送去墓地的场合，以这张卡以外的自己墓地1张「彼岸」卡为对象才能发动。那张卡加入手卡。",
				"packageName": "LEGEND",
				"picId": 83531441,
				"rare": "SR"
			},
			{
				"cardName": "拓扑逻辑轰炸龙",
				"desc": "[拓扑逻辑轰炸龙]\n怪兽/效果/连接 Link-4 暗/电子界族 3000/[↑][↙][↓][↘]\n效果怪兽2只以上\r\n①：这张卡已在怪兽区域存在的状态，这张卡以外的怪兽在连接怪兽所连接区特殊召唤的场合发动。双方的主要怪兽区域的怪兽全部破坏。这个回合，这张卡以外的自己怪兽不能攻击。\r\n②：这张卡向对方怪兽攻击的伤害计算后发动。给与对方那只对方怪兽的原本攻击力数值的伤害。",
				"packageName": "LEGEND",
				"picId": 5821478,
				"rare": "SR"
			},
			{
				"cardName": "教导的骑士 弗勒德莉丝",
				"desc": "[教导的骑士 弗勒德莉丝]\n怪兽/效果 ★8 光/魔法师族 2500/2500\n这个卡名的①②的效果1回合各能使用1次。\r\n①：从额外卡组特殊召唤的怪兽在场上存在的场合，自己·对方的主要阶段才能发动。这张卡从手卡特殊召唤。自己场上有其他的「教导」怪兽存在的场合，可以再选场上1只表侧表示怪兽把那个效果直到回合结束时无效。\r\n②：自己的「教导」怪兽的攻击宣言时才能发动。自己场上的全部「教导」怪兽的攻击力上升500。",
				"packageName": "LEGEND",
				"picId": 69680031,
				"rare": "SR"
			},
			{
				"cardName": "旋坏之贯破黄蜂巢",
				"desc": "[旋坏之贯破黄蜂巢]\n怪兽/效果/超量 ★5 地/机械族 2500/2100\n5星怪兽×2\r\n「旋坏之贯破黄蜂巢」1回合1次也能在自己场上的4阶超量怪兽上面重叠来超量召唤。这张卡在超量召唤的回合不能作为超量素材。这个卡名的②的效果1回合只能使用1次。\r\n①：这张卡向守备表示怪兽攻击的场合，给与攻击力超过那个守备力的数值的战斗伤害。\r\n②：超量召唤的这张卡被对方破坏的场合，以自己墓地1只5星以下的怪兽为对象才能发动。那只怪兽特殊召唤。",
				"packageName": "LEGEND",
				"picId": 39317553,
				"rare": "SR"
			},
			{
				"cardName": "未界域的雷鸟",
				"desc": "[未界域的雷鸟]\n怪兽/效果 ★8 暗/鸟兽族 2800/2400\n这个卡名的②的效果1回合只能使用1次。\r\n①：把手卡的这张卡给对方观看才能发动。从自己的全部手卡之中由对方随机选1张，自己把那张卡丢弃。那是「未界域的雷鸟」以外的场合，再从手卡把1只「未界域的雷鸟」特殊召唤，自己从卡组抽1张。\r\n②：这张卡从手卡丢弃的场合，以对方场上盖放的1张卡为对象才能发动。那张卡破坏。",
				"packageName": "LEGEND",
				"picId": 90807199,
				"rare": "SR"
			},
			{
				"cardName": "杰拉的天使",
				"desc": "[杰拉的天使]\n怪兽/效果/同调 ★8 光/天使族 2800/2300\n调整＋调整以外的怪兽1只以上\r\n「杰拉的天使」的②的效果1回合只能使用1次。\r\n①：这张卡的攻击力上升除外的对方的卡数量×100。\r\n②：这张卡被除外的场合，下个回合的准备阶段发动。除外的这张卡特殊召唤。",
				"packageName": "LEGEND",
				"picId": 42216237,
				"rare": "SR"
			},
			{
				"cardName": "梦幻崩影·独角兽",
				"desc": "[梦幻崩影·独角兽]\n怪兽/效果/连接 Link-3 暗/恶魔族 2200/[←][→][↓]\n卡名不同的怪兽2只以上\r\n这个卡名的①的效果1回合只能使用1次。\r\n①：这张卡连接召唤成功的场合，丢弃1张手卡，以场上1张卡为对象才能发动。那张卡回到持有者卡组。这个效果的发动时这张卡是互相连接状态的场合，再让自己可以从卡组抽1张。\r\n②：自己抽卡阶段的通常抽卡数量只要场上有互相连接状态的「幻崩」怪兽存在变成那些「幻崩」怪兽种类的数量。",
				"packageName": "LEGEND",
				"picId": 38342335,
				"rare": "SR"
			},
			{
				"cardName": "灼银之机龙",
				"desc": "[灼银之机龙]\n怪兽/效果/同调 ★9 炎/机械族 2700/1800\n调整＋调整以外的怪兽1只以上\r\n①：1回合1次，从自己的手卡·墓地以及自己场上的表侧表示怪兽之中把1只调整除外，以场上1张卡为对象才能发动。那张卡破坏。\r\n②：同调召唤的这张卡被效果破坏送去墓地的场合，以除外的1只自己的调整为对象才能发动。那只怪兽加入手卡。",
				"packageName": "LEGEND",
				"picId": 66698383,
				"rare": "SR"
			},
			{
				"cardName": "落消之方块游戏家",
				"desc": "[落消之方块游戏家]\n怪兽/效果/连接 Link-2 光/魔法师族 1300/[→][↓]\n等级不同的怪兽2只\r\n这个卡名的①②的效果1回合各能使用1次。\r\n①：这张卡已在怪兽区域存在的状态，这张卡所连接区有怪兽表侧表示特殊召唤的场合，宣言1～8的任意等级才能发动。那只怪兽直到回合结束时变成宣言的等级。\r\n②：从自己和对方的场上以相同等级的怪兽各1只为对象才能发动。那些怪兽破坏。",
				"packageName": "LEGEND",
				"picId": 84271823,
				"rare": "SR"
			},
			{
				"cardName": "蓦进装甲 剑角犀牛",
				"desc": "[蓦进装甲 剑角犀牛]\n怪兽/效果/同调 ★7 炎/机械族 2400/2100\n调整＋调整以外的怪兽1只以上\r\n这个卡名的①②的效果1回合各能使用1次。\r\n①：把手卡任意数量丢弃才能发动。这张卡的攻击力上升丢弃数量×700。\r\n②：这张卡进行战斗的战斗阶段结束时，把这张卡送去墓地才能发动。等级合计直到变成7星为止，从自己墓地选「蓦进装甲 剑角犀牛」以外的怪兽特殊召唤。这个效果特殊召唤的怪兽的效果无效化。",
				"packageName": "LEGEND",
				"picId": 82197831,
				"rare": "SR"
			},
			{
				"cardName": "狱落鸟",
				"desc": "[狱落鸟]\n怪兽/效果/调整 ★8 暗/幻龙族 2700/1500\n①：这张卡的攻击力·守备力上升自己墓地的调整数量×100。\r\n②：1回合1次，把手卡1只调整送去墓地，以对方场上1只怪兽为对象才能发动。那只怪兽的控制权直到结束阶段得到。",
				"packageName": "再造的世界",
				"picId": 84845628,
				"rare": "HR"
			},
			{
				"cardName": "一骑加势",
				"desc": "[一骑加势]\n魔法\n①：以场上1只表侧表示怪兽为对象才能发动。那只怪兽的攻击力直到回合结束时上升1500。",
				"packageName": "再造的世界",
				"picId": 68833958,
				"rare": "N"
			},
			{
				"cardName": "世代交替",
				"desc": "[世代交替]\n陷阱\n选择自己场上表侧表示存在的1只怪兽破坏。那之后，从卡组把1张和破坏的卡同名的卡加入手卡。",
				"packageName": "再造的世界",
				"picId": 34460239,
				"rare": "N"
			},
			{
				"cardName": "世界树",
				"desc": "[世界树]\n魔法/永续\n每次场上存在的植物族怪兽被破坏，给这张卡放置1个花指示物。可以把这张卡放置的花指示物任意数量取除，以下效果适用。\r\n●1个：直到这个回合的结束阶段时，场上表侧表示存在的1只植物族怪兽的攻击力·守备力上升400。\r\n●2个：把场上1张卡破坏。\r\n●3个：选择自己墓地存在的1只植物族怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 5973663,
				"rare": "N"
			},
			{
				"cardName": "义豪灵蜥",
				"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
				"packageName": "再造的世界",
				"picId": 53776525,
				"rare": "N"
			}
		],
		"pageSize": 20,
		"totalCount": 1857,
		"totalPage": 93
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


## 【玩家端】已知卡片搜索

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/list/user

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
    "packageName": "",
    "cardName": "",
    "rareList": ["UR","SR"],
    "page": 1,
    "pageSize": 10
}
```

#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"cardName": "梦幻崩影·独角兽",
				"desc": "[梦幻崩影·独角兽]\n怪兽/效果/连接 Link-3 暗/恶魔族 2200/[←][→][↓]\n卡名不同的怪兽2只以上\r\n这个卡名的①的效果1回合只能使用1次。\r\n①：这张卡连接召唤成功的场合，丢弃1张手卡，以场上1张卡为对象才能发动。那张卡回到持有者卡组。这个效果的发动时这张卡是互相连接状态的场合，再让自己可以从卡组抽1张。\r\n②：自己抽卡阶段的通常抽卡数量只要场上有互相连接状态的「幻崩」怪兽存在变成那些「幻崩」怪兽种类的数量。",
				"packageName": "LEGEND",
				"picId": 38342335,
				"rare": "SR"
			},
			{
				"cardName": "狱落鸟",
				"desc": "[狱落鸟]\n怪兽/效果/调整 ★8 暗/幻龙族 2700/1500\n①：这张卡的攻击力·守备力上升自己墓地的调整数量×100。\r\n②：1回合1次，把手卡1只调整送去墓地，以对方场上1只怪兽为对象才能发动。那只怪兽的控制权直到结束阶段得到。",
				"packageName": "再造的世界",
				"picId": 84845628,
				"rare": "HR"
			},
			{
				"cardName": "一骑加势",
				"desc": "[一骑加势]\n魔法\n①：以场上1只表侧表示怪兽为对象才能发动。那只怪兽的攻击力直到回合结束时上升1500。",
				"packageName": "再造的世界",
				"picId": 68833958,
				"rare": "N"
			},
			{
				"cardName": "世代交替",
				"desc": "[世代交替]\n陷阱\n选择自己场上表侧表示存在的1只怪兽破坏。那之后，从卡组把1张和破坏的卡同名的卡加入手卡。",
				"packageName": "再造的世界",
				"picId": 34460239,
				"rare": "N"
			},
			{
				"cardName": "世界树",
				"desc": "[世界树]\n魔法/永续\n每次场上存在的植物族怪兽被破坏，给这张卡放置1个花指示物。可以把这张卡放置的花指示物任意数量取除，以下效果适用。\r\n●1个：直到这个回合的结束阶段时，场上表侧表示存在的1只植物族怪兽的攻击力·守备力上升400。\r\n●2个：把场上1张卡破坏。\r\n●3个：选择自己墓地存在的1只植物族怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 5973663,
				"rare": "N"
			},
			{
				"cardName": "义豪灵蜥",
				"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
				"packageName": "再造的世界",
				"picId": 53776525,
				"rare": "N"
			},
			{
				"cardName": "冷蔷薇的抱香",
				"desc": "[冷蔷薇的抱香]\n魔法/速攻\n这个卡名的卡在1回合只能发动1张。\r\n①：把自己场上1只表侧表示怪兽送去墓地才能发动。那只怪兽种族的以下效果适用。\r\n●植物族：这个回合的结束阶段，自己从卡组抽2张，那之后选1张手卡丢弃。\r\n●植物族以外：从卡组把1只4星以下的植物族怪兽加入手卡。",
				"packageName": "再造的世界",
				"picId": 53503015,
				"rare": "N"
			},
			{
				"cardName": "喔喔公鸡",
				"desc": "[喔喔公鸡]\n怪兽/效果/调整 ★5 风/鸟兽族 1600/2000\n①：双方场上没有怪兽存在的场合，这张卡可以作为3星怪兽从手卡特殊召唤。\r\n②：对方场上有怪兽存在，自己场上没有卡存在的场合，这张卡可以作为4星怪兽从手卡特殊召唤。\r\n③：表侧表示的这张卡从场上离开的场合除外。",
				"packageName": "再造的世界",
				"picId": 42338879,
				"rare": "N"
			},
			{
				"cardName": "喔喔雏鸡",
				"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
				"packageName": "再造的世界",
				"picId": 27189308,
				"rare": "N"
			},
			{
				"cardName": "增草剂",
				"desc": "[增草剂]\n魔法/永续\n①：1回合1次，以自己墓地1只植物族怪兽为对象才能发动。那只植物族怪兽特殊召唤。这个效果把怪兽特殊召唤的回合，自己不能通常召唤。这个效果特殊召唤的怪兽从场上离开时这张卡破坏。",
				"packageName": "再造的世界",
				"picId": 44887817,
				"rare": "N"
			},
			{
				"cardName": "奈芙提斯之引导者",
				"desc": "[奈芙提斯之引导者]\n怪兽/效果 ★2 风/魔法师族 600/600\n①：把自己场上1只怪兽和这张卡解放才能发动。从手卡·卡组把1只「奈芙提斯之凤凰神」特殊召唤。",
				"packageName": "再造的世界",
				"picId": 98446407,
				"rare": "N"
			},
			{
				"cardName": "奈芙提斯之悟道者",
				"desc": "[奈芙提斯之悟道者]\n怪兽/效果 ★2 风/魔法师族 600/600\n这个卡名的①②的效果1回合各能使用1次。\r\n①：以「奈芙提斯之悟道者」以外的自己墓地1只4星以下的「奈芙提斯」怪兽为对象才能发动。选1张手卡破坏，作为对象的怪兽守备表示特殊召唤。这个效果特殊召唤的怪兽的效果无效化。\r\n②：这张卡被效果破坏送去墓地的场合，下次的自己准备阶段才能发动。这张卡从墓地特殊召唤。",
				"packageName": "再造的世界",
				"picId": 52904476,
				"rare": "N"
			},
			{
				"cardName": "奈芙提斯之护卫者",
				"desc": "[奈芙提斯之护卫者]\n怪兽/效果 ★2 风/魔法师族 1400/200\n这个卡名的①②的效果1回合各能使用1次。\r\n①：自己主要阶段才能发动。选1张手卡破坏，从手卡把「奈芙提斯之护卫者」以外的1只4星以下的「奈芙提斯」怪兽特殊召唤。\r\n②：这张卡被效果破坏送去墓地的场合，下次的自己准备阶段才能发动。从卡组选「奈芙提斯之护卫者」以外的1只「奈芙提斯」怪兽破坏。",
				"packageName": "再造的世界",
				"picId": 51782995,
				"rare": "N"
			},
			{
				"cardName": "奈芙提斯的觉醒",
				"desc": "[奈芙提斯的觉醒]\n陷阱/永续\n这个卡名的②的效果1回合只能使用1次。\r\n①：只要这张卡在魔法与陷阱区域存在，自己场上的「奈芙提斯」怪兽的攻击力上升300。\r\n②：魔法与陷阱区域的表侧表示的这张卡被效果破坏送去墓地的场合才能发动。从自己的手卡·卡组·墓地选1只「奈芙提斯」怪兽特殊召唤。这个效果特殊召唤的怪兽在结束阶段破坏。",
				"packageName": "再造的世界",
				"picId": 82832464,
				"rare": "N"
			},
			{
				"cardName": "妖海的巨魔歹徒",
				"desc": "[妖海的巨魔歹徒]\n怪兽/效果/调整 ★3 水/兽战士族 1000/1300\n这个卡名的①②的效果1回合各能使用1次，这些效果发动的回合，自己不是兽战士族怪兽不能特殊召唤。\r\n①：这张卡召唤·特殊召唤成功的场合，以自己墓地1只兽战士族怪兽为对象才能发动。这张卡的属性·等级直到回合结束时变成和那只怪兽相同。\r\n②：自己主要阶段才能发动。把持有和这张卡相同属性·等级的1只兽战士族怪兽从手卡特殊召唤。",
				"packageName": "再造的世界",
				"picId": 43464884,
				"rare": "N"
			},
			{
				"cardName": "妥善料理的队长",
				"desc": "[妥善料理的队长]\n怪兽/效果 ★3 地/战士族 1200/400\n①：这张卡召唤成功时才能发动。让1张手卡回到卡组洗切。那之后，自己从卡组抽1张。那张抽到的卡是怪兽的场合，可以把那只怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 48737767,
				"rare": "N"
			},
			{
				"cardName": "威炎星-飞燕灼",
				"desc": "[威炎星-飞燕灼]\n怪兽/效果 ★5 炎/兽战士族 2000/800\n这张卡可以把自己场上表侧表示存在的3张名字带有「炎舞」的魔法·陷阱卡送去墓地，从手卡特殊召唤。这张卡召唤·特殊召唤成功时，可以从卡组选1张名字带有「炎舞」的陷阱卡在自己场上盖放。「威炎星-飞燕灼」的这个效果1回合只能使用1次。此外，只要这张卡在场上表侧表示存在，自己场上的兽战士族怪兽不会成为对方的卡的效果的对象。",
				"packageName": "再造的世界",
				"picId": 2521011,
				"rare": "N"
			},
			{
				"cardName": "孤炎星-鲁猪深",
				"desc": "[孤炎星-鲁猪深]\n怪兽/效果/调整 ★4 炎/兽战士族 1100/1400\n把这张卡作为同调素材的场合，不是炎属性怪兽的同调召唤不能使用。这张卡被战斗破坏送去墓地时，可以从卡组把「孤炎星-鲁猪深」以外的1只名字带有「炎星」的4星怪兽特殊召唤。1回合1次，这张卡在场上存在的场合名字带有「炎星」的怪兽从自己的额外卡组特殊召唤时，可以从卡组选1张名字带有「炎舞」的魔法卡在自己场上盖放。",
				"packageName": "再造的世界",
				"picId": 66762372,
				"rare": "N"
			},
			{
				"cardName": "巨歧蜥·魔蜥义豪",
				"desc": "[巨歧蜥·魔蜥义豪]\n怪兽/通常 ★8 水/爬虫类族 2950/2800\n已经精神崩溃，肉体追求更强的力量而暴走。在它身上再也找不到昔日的面影……",
				"packageName": "再造的世界",
				"picId": 39674352,
				"rare": "N"
			},
			{
				"cardName": "怨念之魂 业火",
				"desc": "[怨念之魂 业火]\n怪兽/效果 ★6 炎/不死族 2200/1900\n①：自己场上有炎属性怪兽存在的场合，这张卡可以从手卡特殊召唤。\r\n②：这张卡的①的方法特殊召唤成功的场合，以自己场上1只炎属性怪兽为对象发动。那只自己的炎属性怪兽破坏。\r\n③：把这张卡以外的自己场上1只炎属性怪兽解放才能发动。这张卡的攻击力直到回合结束时上升500。\r\n④：自己准备阶段发动。在自己场上把1只「火之玉衍生物」（炎族·炎·1星·攻/守100）守备表示特殊召唤。",
				"packageName": "再造的世界",
				"picId": 23116808,
				"rare": "N"
			}
		],
		"pageSize": 20,
		"totalCount": 1805,
		"totalPage": 91
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


## 【ALL】卡片列表搜索
根据传入的账户信息，判断是否为管理员。若是管理员，则返回全卡列表， 否则返回已知卡片列表
#### 接口URL
> http://127.0.0.1:15163/weird_project/card/list

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
    "packageName": "",
    "cardName": "",
    "rareList": ["UR","SR"],
    "page": 1,
    "pageSize": 10
}
```

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
				"desc": "",
				"picId": 0,
				"rare": "N"
			},
			{
				"cardName": "巨歧蜥·魔蜥义豪",
				"packageName": "再造的世界",
				"desc": "",
				"picId": 0,
				"rare": "N"
			},
			{
				"cardName": "歧蜥·魔蜥义豪",
				"packageName": "再造的世界",
				"desc": "",
				"picId": 0,
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
> http://127.0.0.1:15163/weird_project/card/add?name=admin&password=1&package=霸王的威压&card=入魔鬼·血石&rare=N

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


## 【管理端】批量添加卡片信息

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/add

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
	"name": "admin",
	"password": "E10ADC3949BA59ABBE56E057F20F883E",
	"packageName": "咕咕",
	"nList": [
		"N"
	],
	"rList": [
		"R1",
		"R2"
	],
	"srList": [
		"SR1",
		"SR2"
	],
	"urList": [
		"UR1",
		"UR2"
	],
	"hrList": [
		"HR"
	]
}
```

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
	"data": "输入中存在重复卡片：[SR1, SR2]"
}
```


## 【管理端】修改卡片名称

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/update?name=admin&password=1&oldname=草兽&newname=除草兽&show=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| oldname     | 草兽 | 必填 | 旧卡名 |
| newname     | 除草兽 | 必填 | 新卡名 |
| show     | - | 选填 | 是否记录在更新记录（0以外为记录，默认为0） |






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


## 【管理端】互换两张卡的稀有度

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/exchange?name=admin&password=1&card1=草兽&card2=除草兽&show=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
| card1     | 草兽 | 必填 | 卡名1 |
| card2     | 除草兽 | 必填 | 卡名2 |
| show     | - | 选填 | 是否记录在更新记录（0以外为记录，默认为0） |






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
> http://127.0.0.1:15163/weird_project/card/ownlist?package=&card=&rare=&target=&page=&pagesize=

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
| pagesize     | - | 选填 | 页面大小，默认20 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"cardName": "一骑加势",
				"count": 3,
				"desc": "[一骑加势]\n魔法\n①：以场上1只表侧表示怪兽为对象才能发动。那只怪兽的攻击力直到回合结束时上升1500。",
				"packageName": "再造的世界",
				"picId": 68833958,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "一骑加势",
				"count": 3,
				"desc": "[一骑加势]\n魔法\n①：以场上1只表侧表示怪兽为对象才能发动。那只怪兽的攻击力直到回合结束时上升1500。",
				"packageName": "再造的世界",
				"picId": 68833958,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "世代交替",
				"count": 3,
				"desc": "[世代交替]\n陷阱\n选择自己场上表侧表示存在的1只怪兽破坏。那之后，从卡组把1张和破坏的卡同名的卡加入手卡。",
				"packageName": "再造的世界",
				"picId": 34460239,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "世代交替",
				"count": 3,
				"desc": "[世代交替]\n陷阱\n选择自己场上表侧表示存在的1只怪兽破坏。那之后，从卡组把1张和破坏的卡同名的卡加入手卡。",
				"packageName": "再造的世界",
				"picId": 34460239,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "世代交替",
				"count": 3,
				"desc": "[世代交替]\n陷阱\n选择自己场上表侧表示存在的1只怪兽破坏。那之后，从卡组把1张和破坏的卡同名的卡加入手卡。",
				"packageName": "再造的世界",
				"picId": 34460239,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "世界树",
				"count": 3,
				"desc": "[世界树]\n魔法/永续\n每次场上存在的植物族怪兽被破坏，给这张卡放置1个花指示物。可以把这张卡放置的花指示物任意数量取除，以下效果适用。\r\n●1个：直到这个回合的结束阶段时，场上表侧表示存在的1只植物族怪兽的攻击力·守备力上升400。\r\n●2个：把场上1张卡破坏。\r\n●3个：选择自己墓地存在的1只植物族怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 5973663,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "世界树",
				"count": 3,
				"desc": "[世界树]\n魔法/永续\n每次场上存在的植物族怪兽被破坏，给这张卡放置1个花指示物。可以把这张卡放置的花指示物任意数量取除，以下效果适用。\r\n●1个：直到这个回合的结束阶段时，场上表侧表示存在的1只植物族怪兽的攻击力·守备力上升400。\r\n●2个：把场上1张卡破坏。\r\n●3个：选择自己墓地存在的1只植物族怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 5973663,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "世界树",
				"count": 3,
				"desc": "[世界树]\n魔法/永续\n每次场上存在的植物族怪兽被破坏，给这张卡放置1个花指示物。可以把这张卡放置的花指示物任意数量取除，以下效果适用。\r\n●1个：直到这个回合的结束阶段时，场上表侧表示存在的1只植物族怪兽的攻击力·守备力上升400。\r\n●2个：把场上1张卡破坏。\r\n●3个：选择自己墓地存在的1只植物族怪兽特殊召唤。",
				"packageName": "再造的世界",
				"picId": 5973663,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "义豪灵蜥",
				"count": 3,
				"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
				"packageName": "再造的世界",
				"picId": 53776525,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "义豪灵蜥",
				"count": 3,
				"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
				"packageName": "再造的世界",
				"picId": 53776525,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "义豪灵蜥",
				"count": 3,
				"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
				"packageName": "再造的世界",
				"picId": 53776525,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "冷蔷薇的抱香",
				"count": 3,
				"desc": "[冷蔷薇的抱香]\n魔法/速攻\n这个卡名的卡在1回合只能发动1张。\r\n①：把自己场上1只表侧表示怪兽送去墓地才能发动。那只怪兽种族的以下效果适用。\r\n●植物族：这个回合的结束阶段，自己从卡组抽2张，那之后选1张手卡丢弃。\r\n●植物族以外：从卡组把1只4星以下的植物族怪兽加入手卡。",
				"packageName": "再造的世界",
				"picId": 53503015,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "冷蔷薇的抱香",
				"count": 1,
				"desc": "[冷蔷薇的抱香]\n魔法/速攻\n这个卡名的卡在1回合只能发动1张。\r\n①：把自己场上1只表侧表示怪兽送去墓地才能发动。那只怪兽种族的以下效果适用。\r\n●植物族：这个回合的结束阶段，自己从卡组抽2张，那之后选1张手卡丢弃。\r\n●植物族以外：从卡组把1只4星以下的植物族怪兽加入手卡。",
				"packageName": "再造的世界",
				"picId": 53503015,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "冷蔷薇的抱香",
				"count": 3,
				"desc": "[冷蔷薇的抱香]\n魔法/速攻\n这个卡名的卡在1回合只能发动1张。\r\n①：把自己场上1只表侧表示怪兽送去墓地才能发动。那只怪兽种族的以下效果适用。\r\n●植物族：这个回合的结束阶段，自己从卡组抽2张，那之后选1张手卡丢弃。\r\n●植物族以外：从卡组把1只4星以下的植物族怪兽加入手卡。",
				"packageName": "再造的世界",
				"picId": 53503015,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "喔喔公鸡",
				"count": 3,
				"desc": "[喔喔公鸡]\n怪兽/效果/调整 ★5 风/鸟兽族 1600/2000\n①：双方场上没有怪兽存在的场合，这张卡可以作为3星怪兽从手卡特殊召唤。\r\n②：对方场上有怪兽存在，自己场上没有卡存在的场合，这张卡可以作为4星怪兽从手卡特殊召唤。\r\n③：表侧表示的这张卡从场上离开的场合除外。",
				"packageName": "再造的世界",
				"picId": 42338879,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "喔喔公鸡",
				"count": 1,
				"desc": "[喔喔公鸡]\n怪兽/效果/调整 ★5 风/鸟兽族 1600/2000\n①：双方场上没有怪兽存在的场合，这张卡可以作为3星怪兽从手卡特殊召唤。\r\n②：对方场上有怪兽存在，自己场上没有卡存在的场合，这张卡可以作为4星怪兽从手卡特殊召唤。\r\n③：表侧表示的这张卡从场上离开的场合除外。",
				"packageName": "再造的世界",
				"picId": 42338879,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "喔喔公鸡",
				"count": 3,
				"desc": "[喔喔公鸡]\n怪兽/效果/调整 ★5 风/鸟兽族 1600/2000\n①：双方场上没有怪兽存在的场合，这张卡可以作为3星怪兽从手卡特殊召唤。\r\n②：对方场上有怪兽存在，自己场上没有卡存在的场合，这张卡可以作为4星怪兽从手卡特殊召唤。\r\n③：表侧表示的这张卡从场上离开的场合除外。",
				"packageName": "再造的世界",
				"picId": 42338879,
				"rare": "N",
				"userName": "虱子"
			},
			{
				"cardName": "喔喔雏鸡",
				"count": 3,
				"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
				"packageName": "再造的世界",
				"picId": 27189308,
				"rare": "N",
				"userName": "小修"
			},
			{
				"cardName": "喔喔雏鸡",
				"count": 1,
				"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
				"packageName": "再造的世界",
				"picId": 27189308,
				"rare": "N",
				"userName": "烧鸡"
			},
			{
				"cardName": "喔喔雏鸡",
				"count": 3,
				"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
				"packageName": "再造的世界",
				"picId": 27189308,
				"rare": "N",
				"userName": "虱子"
			}
		],
		"pageSize": 20,
		"totalCount": 6083,
		"totalPage": 305
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


## 【ALL】卡片修改记录搜索

#### 接口URL
> http://127.0.0.1:15163/weird_project/card/history

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
    "packageName": "",
    "cardName": "",
    "rareList": ["SR","HR"],
    "page": 1,
    "pageSize": 20
}
```

#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"createdTime": "2020-09-25 12:04:47",
				"newDesc": "[胡萝卜人]\n怪兽/效果 ★4 暗/植物族 1900/0\n「胡萝卜人」的效果1回合只能使用1次。\r\n①：这张卡在墓地存在，把自己手卡·场上的「胡萝卜人」以外的1只植物族怪兽送去墓地才能发动。这张卡从墓地特殊召唤。",
				"newName": "胡萝卜人",
				"newPicId": 44928016,
				"oldDesc": "[增草剂]\n魔法/永续\n①：1回合1次，以自己墓地1只植物族怪兽为对象才能发动。那只植物族怪兽特殊召唤。这个效果把怪兽特殊召唤的回合，自己不能通常召唤。这个效果特殊召唤的怪兽从场上离开时这张卡破坏。",
				"oldName": "增草剂",
				"oldPicId": 44887817,
				"packageName": "再造的世界",
				"rare": "R"
			},
			{
				"createdTime": "2020-09-25 12:04:47",
				"newDesc": "[增草剂]\n魔法/永续\n①：1回合1次，以自己墓地1只植物族怪兽为对象才能发动。那只植物族怪兽特殊召唤。这个效果把怪兽特殊召唤的回合，自己不能通常召唤。这个效果特殊召唤的怪兽从场上离开时这张卡破坏。",
				"newName": "增草剂",
				"newPicId": 44887817,
				"oldDesc": "[胡萝卜人]\n怪兽/效果 ★4 暗/植物族 1900/0\n「胡萝卜人」的效果1回合只能使用1次。\r\n①：这张卡在墓地存在，把自己手卡·场上的「胡萝卜人」以外的1只植物族怪兽送去墓地才能发动。这张卡从墓地特殊召唤。",
				"oldName": "胡萝卜人",
				"oldPicId": 44928016,
				"packageName": "再造的世界",
				"rare": "N"
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
	"data": "Required int parameter 'page' is not present"
}
```


## 【管理端】新增卡包

#### 接口URL
> http://127.0.0.1:15163/weird_project/package/add?name=admin&password=1&package=咕咕咕

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


## 【管理端】卡组排序

#### 接口URL
> http://127.0.0.1:15163/weird_project/package/sort

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
	"name": "admin",
	"password": "e10adc3949ba59abbe56e057f20f883e",
	"packageIndexList": [
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10,
		11,
		12,
		13,
		14,
		15,
		16,
		17,
		18,
		19,
		20,
		21,
		22,
		23,
		24,
		25,
		26,
		27,
		28,
		29,
		1
	]
}
```

#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "修改排序成功！"
}
```



## 【管理端】修改卡包名

#### 接口URL
> http://127.0.0.1:15163/weird_project/package/update?name=admin&password=1&oldname=咕咕咕&newname=咕咕咕咕

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


## 【ALL】查询卡包列表

#### 接口URL
> http://127.0.0.1:15163/weird_project/package/list?name=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | - | 选填 | 卡包名，模糊搜索 |




#### 请求Header参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | - |  选填 | 卡包名，模糊搜索 |


#### 成功响应示例
```javascript
{
	"code": 200,
	"data": [
		{
			"packageId": 1,
			"packageName": "咕咕咕"
		}
	]
}
```



## 【管理端】添加新用户
创建的用户密码默认为``E10ADC3949BA59ABBE56E057F20F883E``（``123456``经过32位大写MD5加密后）
#### 接口URL
> http://127.0.0.1:15163/weird_project/user/add?name=admin&password=1&target=新的用户

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
> http://127.0.0.1:15163/weird_project/user/card/update?name=admin&password=1&card=除草兽&target=test&count=2

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 操作用户名称 |
| password     | 1 | 必填 | 操作用户密码 |
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


## 【管理端】批量修改用户持有的卡片数量

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/card/update

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
	"name": "admin",
	"password": "e10adc3949ba59abbe56e057f20f883e",
	"target": "烧鸡",
	"counts": {
		"喔喔雏鸡": 1,
		"喔喔公鸡": 3,
		"狱落鸟": 1
	}
}
```

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
	"code": 200,
	"data": "[烧鸡]的卡片[喔喔雏鸡]的数量没有变化！\n成功2条数据，失败1条数据。"
}
```


## 【管理端】交换两个玩家持有的卡片

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/card/swap

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
	"name": "admin",
	"password": "e10adc3949ba59abbe56e057f20f883e",
	"userA": "虱子",
	"cardA": "王魂调和",
	"userB": "小修",
	"cardB": "蔷薇刻印"
}
```

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
	"data": "权限不足！"
}
```


## 【管理端】修改用户的尘数

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/dust?name=admin&password=1&target=&count=1

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
> http://127.0.0.1:15163/weird_project/user/award?name=admin&password=1&target=新的用户&award=1

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
> http://127.0.0.1:15163/weird_project/user/check?name=&password=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | - | 必填 | 用户名 |
| password     | - | 必填 | 用户密码 |






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
> http://127.0.0.1:15163/weird_project/user/list?user=&page=&pagesize=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| user     | - | 选填 | 用户名，模糊搜索 |
| page     | - | 选填 | 页码 |
| pagesize     | - | 选填 | 页面大小，默认20 |






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


## 【ALL】修改用户密码

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/pw?name=admin&old=E10ADC3949BA59ABBE56E057F20F883E&new=e10adc3949ba59abbe56e057f20f883e

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | admin | 必填 | 用户名 |
| old     | E10ADC3949BA59ABBE56E057F20F883E | 必填 | 旧密码 |
| new     | e10adc3949ba59abbe56e057f20f883e | 必填 | 新密码 |






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
	"data": "用户名或密码错误！"
}
```


## 【玩家端】直接将尘转换为卡片

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/card/change?name=用户&password=E10ADC3949BA59ABBE56E057F20F883E&card=草泥马

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | 用户 | 必填 | 操作用户名称 |
| password     | E10ADC3949BA59ABBE56E057F20F883E | 必填 | 操作用户密码 |
| card     | 草泥马 | 必填 | 卡片名 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "转换成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "[用户]当前已拥有3张[草泥马]，无法再合成！"
}
```


## 【玩家端】随机卡包抽闪

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/card/random?name=小白鼠&password=e10adc3949ba59abbe56e057f20f883e&package=联合之力&dustFirst=

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | 小白鼠 | 必填 | 操作用户名称 |
| password     | e10adc3949ba59abbe56e057f20f883e | 必填 | 操作用户密码 |
| package     | 联合之力 | 必填 | 卡包名 |
| dustFirst     | - | 选填 | 是否优先用尘进行合成，默认为0，大于0则为真 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "你抽到了[强制退出装置](SR)！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "[新的用户]本周的随机合成次数已用完！"
}
```


## 【玩家端】溢出闪换尘

#### 接口URL
> http://127.0.0.1:15163/weird_project/user/card/todust?name=小白鼠&password=e10adc3949ba59abbe56e057f20f883e&card=蔷薇刻印&count=2

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| name     | 小白鼠 | 必填 | 操作用户名称 |
| password     | e10adc3949ba59abbe56e057f20f883e | 必填 | 操作用户密码 |
| card     | 蔷薇刻印 | 必填 | 卡片名 |
| count     | 2 | 必填 | 转换的数量 |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "共获得100尘！"
}
```


#### 错误响应示例
```javascript
{
	"code": 500,
	"data": "拥有的[蔷薇刻印]不足以分解！"
}
```


## 【管理端】设置某个抽卡结果是否适用

#### 接口URL
> http://127.0.0.1:15163/weird_project/roll/set?name=admin&password=1&id=3&status=0

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
> http://127.0.0.1:15163/weird_project/roll/list?package=&user=&page=1&pagesize=&start=1600853029&end=0

#### 请求方式
> GET

#### Content-Type
> form-data

#### 请求Query参数

| 参数        | 示例值   | 是否必填   |  参数描述  |
| :--------   | :-----  | :-----  | :----  |
| package     | - | 选填 | 卡包名，模糊搜索 |
| user     | - | 选填 | 用户名，模糊搜索 |
| page     | 1 | 选填 | 页码，默认为1 |
| pagesize     | - | 选填 | 页面大小，默认20 |
| start     | 1600853029 | 选填 | 抽卡开始时间（10位时间戳） |
| end     | 0 | 选填 | 抽卡结束时间（10位时间戳） |






#### 成功响应示例
```javascript
{
	"code": 200,
	"data": {
		"currPage": 1,
		"dataList": [
			{
				"isDisabled": 0,
				"rollId": 2,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "魔蜥义豪",
						"desc": "[魔蜥义豪]\n怪兽/通常 ★4 水/爬虫类族 1850/1000\n虽然曾经拥有邪恶的心灵，但是由于遇见某个人物而觉醒了正义之心的年轻恶魔。",
						"picId": 49003308,
						"rare": "N"
					},
					{
						"cardName": "蔷薇恋人",
						"desc": "[蔷薇恋人]\n怪兽/效果 ★1 地/植物族 800/800\n这个卡名的效果1回合只能使用1次。\r\n①：把墓地的这张卡除外才能发动。从手卡把1只植物族怪兽特殊召唤。这个效果特殊召唤的怪兽在这个回合不受对方的陷阱卡的效果影响。",
						"picId": 89252157,
						"rare": "N"
					},
					{
						"cardName": "喔喔雏鸡",
						"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
						"picId": 27189308,
						"rare": "N"
					}
				],
				"rollUserName": "虱子",
				"time": "2020-09-25 12:05:50"
			},
			{
				"isDisabled": 0,
				"rollId": 1,
				"rollPackageName": "再造的世界",
				"rollResult": [
					{
						"cardName": "喔喔雏鸡",
						"desc": "[喔喔雏鸡]\n怪兽/效果/反转 ★1 风/鸟兽族 300/200\n反转：可以从卡组把1只5星以上的调整特殊召唤。",
						"picId": 27189308,
						"rare": "N"
					},
					{
						"cardName": "义豪灵蜥",
						"desc": "[义豪灵蜥]\n怪兽/通常 ★1 水/爬虫类族 350/300\n现在虽然还拥有温和的心，但却背负着被邪恶之心侵蚀的命运。",
						"picId": 53776525,
						"rare": "N"
					},
					{
						"cardName": "喔喔公鸡",
						"desc": "[喔喔公鸡]\n怪兽/效果/调整 ★5 风/鸟兽族 1600/2000\n①：双方场上没有怪兽存在的场合，这张卡可以作为3星怪兽从手卡特殊召唤。\r\n②：对方场上有怪兽存在，自己场上没有卡存在的场合，这张卡可以作为4星怪兽从手卡特殊召唤。\r\n③：表侧表示的这张卡从场上离开的场合除外。",
						"picId": 42338879,
						"rare": "N"
					}
				],
				"rollUserName": "虱子",
				"time": "2020-09-25 12:05:50"
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


## 【管理端】发送抽卡信息

#### 接口URL
> http://127.0.0.1:15163/weird_project/weirdUI/roll?name=admin&password=1&target=新的用户&card1=怨念之魂 业火&card2=喔喔雏鸡&card3=增草剂

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


## 【管理端】批量发送抽卡信息（POST）

#### 接口URL
> http://127.0.0.1:15163/weird_project/roll/add

#### 请求方式
> POST

#### Content-Type
> json






#### 请求Body参数

```javascript
{
	"name": "admin",
	"password": "e10adc3949ba59abbe56e057f20f883e",
	"target": "新的用户",
	"cards": [
		[
			"喔喔雏鸡",
			"义豪灵蜥",
			"罡炎星-麒俊麟"
		],
		[
			"魔蜥义豪",
			"蔷薇恋人",
			"咕咕咕"
		]
	]
}
```

#### 成功响应示例
```javascript
{
	"code": 200,
	"data": "全部记录成功！"
}
```


#### 错误响应示例
```javascript
{
	"code": 200,
	"data": "抽卡记录[[魔蜥义豪, 蔷薇恋人, 咕咕咕]]中找不到该卡片：咕咕咕！\n共成功记录1个结果！"
}
```


## 【ALL】HOME
测试项目是否正常启动的接口
#### 接口URL
> http://127.0.0.1:15163/weird_project/

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


