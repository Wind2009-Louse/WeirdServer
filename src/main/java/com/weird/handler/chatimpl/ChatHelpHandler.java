package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.handler.RunnerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 帮助信息
 *
 * @author Nidhogg
 * @date 2021.10.13
 */
@Component
public class ChatHelpHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    RunnerHandler runnerHandler;

    final static String SPLIT_STR = ">帮助";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        if (message.startsWith(SPLIT_STR)) {
            String args = message.substring(SPLIT_STR.length()).trim();
            String printInfo = "";
            switch (args) {
                case "认证":
                case "绑定":
                    printInfo = "认证/绑定：\n>认证 用户名 密码\n绑定云诡异的对应帐号。";
                    break;
                case "信息":
                    printInfo = "信息：\n>信息\n查询当前认证的帐号信息。";
                    break;
                case "解绑":
                    printInfo = "解绑：\n>解绑\n解除当前认证的帐号信息。";
                    break;
                case "转盘":
                    printInfo = "转盘：\n>转盘\n使用当前认证的帐号进行转盘操作。";
                    break;

                case "查卡":
                    printInfo = "查卡：\n>查卡 查询内容\n简易查卡，一次最多显示10条信息。\n支持使用空格分割关键词和高级搜索，如：\n>查诡异 等级：>=5 黑魔术师\n高级搜索帮助请使用：\n>帮助 高级搜索";
                    break;
                case "查诡异":
                    printInfo = "查诡异：\n>查诡异 查询内容\n简易诡异卡池查询，可以查询卡片稀有度、卡包和总数，在已认证帐号的情况下可以显示自身持有的数量。每次搜索最多显示10条信息。\n支持使用空格分割关键词和高级搜索，如：\n>查诡异 等级：>=5 黑魔术师\n高级搜索帮助请使用：\n>帮助 高级搜索";
                    break;
                case "高级搜索":
                    printInfo = "高级搜索方法：\n字段：内容\n以下字段支持范围搜索（>=等）：攻击、守备、等级、刻度、阶级、连接\n以下字段支持模糊搜索：卡名\n以下字段支持精确搜索：类型、种族、属性";
                    break;

                case "查房":
                    printInfo = "查房：\n>查房\n查询当前群组90分钟内聊天记录中的房间号。";
                    break;
                case "查闪率":
                    printInfo = "查闪率：\n>查闪率 天数\n查询时间范围内的闪率统计。为减轻数据库负担，闪率统计只能半小时执行一次。";
                    break;
                case "查冒险":
                    printInfo = "查冒险：\n>查冒险 当前地点 [前进步数]\n查询前进之后的冒险地点信息，不填前进步数时为当前地点信息。";
                    break;

                case "syn":
                    printInfo = ">帮助 ack";
                    break;
                case "帮助":
                    printInfo = "帮助的帮助是帮助的帮助。";
                    break;
                case "功能名":
                    printInfo = "请输入具体的功能名称，“功能名”自身并不是一个功能。";
                    break;

                case "抽卡":
                    printInfo = "抽卡功能：\n>抽卡 卡包名/卡包编号/卡包描述 数量 [闪停/百八/全]\n玩家发起抽卡请求（每个玩家只能同时发起一次）。\n* 百八：每周日的前10包卡有8%的闪率。\n* 闪停：抽到闪卡时不再继续。\n* 全：显示全部抽卡结果" +
                            "\n>抽卡/>抽卡 列表\n查看当前已经发起的抽卡请求。\n>抽卡 取消 编号\n发起者或者管理员取消抽卡请求。\n>抽卡 编号/ALL\n管理员进行指定的抽卡操作。";
                    break;
                default:
                    String startTime = runnerHandler.getStartTime();
                    printInfo = String.format("服务启动于%s\n目前功能：\n帐号相关：认证/绑定、信息、解绑\n查询：查卡、查诡异、查房、查闪率、查冒险\n简易功能：抽卡、转盘\n请使用以下方法查看相关功能的帮助：\n>帮助 功能名", startTime);
                    break;
            }
            broadcastFacade.sendMsgAsync(buildResponse(printInfo, o));
        }
    }
}
