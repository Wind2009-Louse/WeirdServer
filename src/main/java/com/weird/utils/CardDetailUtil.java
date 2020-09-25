package com.weird.utils;

import com.weird.model.CardDetailModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 卡片效果工具
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public class CardDetailUtil {
    static Map<Integer, String> cardTypes = new LinkedHashMap<Integer, String>() {{
        put(0x1, "怪兽");
        put(0x2, "魔法");
        put(0x4, "陷阱");
        put(0x10, "通常");
        put(0x20, "效果");
        put(0x40, "融合");
        put(0x80, "仪式");
        put(0x200, "灵魂");
        put(0x400, "同盟");
        put(0x800, "二重");
        put(0x1000, "调整");
        put(0x2000, "同调");
        put(0x4000, "衍生物");
        put(0x10000, "速攻");
        put(0x20000, "永续");
        put(0x40000, "装备");
        put(0x80000, "场地");
        put(0x100000, "反击");
        put(0x200000, "反转");
        put(0x400000, "卡通");
        put(0x800000, "超量");
        put(0x1000000, "灵摆");
        put(0x2000000, "特殊召唤");
        put(0x4000000, "连接");
    }};
    static Map<Integer, String> cardRaces = new LinkedHashMap<Integer, String>() {{
        put(0x1, "战士族");
        put(0x2, "魔法师族");
        put(0x4, "天使族");
        put(0x8, "恶魔族");
        put(0x10, "不死族");
        put(0x20, "机械族");
        put(0x40, "水族");
        put(0x80, "炎族");
        put(0x100, "岩石族");
        put(0x200, "鸟兽族");
        put(0x400, "植物族");
        put(0x800, "昆虫族");
        put(0x1000, "雷族");
        put(0x2000, "龙族");
        put(0x4000, "兽族");
        put(0x8000, "兽战士族");
        put(0x10000, "恐龙族");
        put(0x20000, "鱼族");
        put(0x40000, "海龙族");
        put(0x80000, "爬虫类族");
        put(0x100000, "念动力族");
        put(0x200000, "幻神兽族");
        put(0x400000, "创造神族");
        put(0x800000, "幻龙族");
        put(0x1000000, "电子界族");
    }};
    static Map<Integer, String> cardAttributes = new LinkedHashMap<Integer, String>() {{
        put(0x1, "地");
        put(0x2, "水");
        put(0x4, "炎");
        put(0x8, "风");
        put(0x10, "光");
        put(0x20, "暗");
        put(0x40, "神");
    }};
    static Map<Integer, String> linkMarkers = new LinkedHashMap<Integer, String>() {{
        put(0x40, "[↖]");
        put(0x80, "[↑]");
        put(0x100, "[↗]");
        put(0x8, "[←]");
        put(0x20, "[→]");
        put(0x1, "[↙]");
        put(0x2, "[↓]");
        put(0x4, "[↘]");
    }};

    public static String getResult(CardDetailModel model) {
        if (model == null) {
            return "";
        }
        String result = CacheUtil.DetailCache.get(model.getName());
        if (result != null) {
            return result;
        }

        StringBuilder sb = new StringBuilder();

        // 卡名
        sb.append("[");
        sb.append(model.getName());
        sb.append("]\n");

        // 种类
        mapAppend(cardTypes, model.getType(), "/", sb);

        // 怪兽资料
        if ((model.getType() & 0x1) != 0) {
            // 等阶link
            if ((model.getType() & 0x4000000) != 0) {
                sb.append(" Link-");
                sb.append(model.getLevel());
            } else {
                sb.append(" ★");
                sb.append(model.getLevel() & 0xffff);
            }

            // 属性/种族
            sb.append(" ");
            mapAppend(cardAttributes, model.getAttribute(), "&", sb);
            sb.append("/");
            mapAppend(cardRaces, model.getRace(), "&", sb);
            sb.append(" ");

            // ATKDEF
            if (model.getAtk() < 0) {
                sb.append(" ");
            } else {
                sb.append(model.getAtk());
            }
            sb.append("/");
            if ((model.getType() & 0x4000000) != 0) {
                mapAppend(linkMarkers, model.getDef(), "", sb);
            } else {
                if (model.getDef() < 0) {
                    sb.append("?");
                } else {
                    sb.append(model.getDef());
                }
            }
        }
        sb.append("\n");
        sb.append(model.getDesc());

        result = sb.toString();
        CacheUtil.DetailCache.put(model.getName(), result);
        return result;
    }

    private static void mapAppend(Map<Integer, String> map, int target, String joiner, StringBuilder sb) {
        boolean get = false;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if ((target & entry.getKey()) != 0) {
                if (get) {
                    sb.append(joiner);
                }
                sb.append(entry.getValue());
                get = true;
            }
        }
    }
}
