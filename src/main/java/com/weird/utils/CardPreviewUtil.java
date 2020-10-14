package com.weird.utils;

import com.weird.model.CardPreviewModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 卡片预览工具
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public class CardPreviewUtil {
    public static int HIDE_PREVIEW_COUNT = 100;

    static int MONSTER_TYPE = 0x1;
    static int LINK_TYPE = 0x4000000;
    static Map<Integer, String> CARD_TYPES = new LinkedHashMap<Integer, String>() {{
        put(MONSTER_TYPE, "怪兽");
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
        put(LINK_TYPE, "连接");
    }};
    static Map<Integer, String> CARD_RACES = new LinkedHashMap<Integer, String>() {{
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
    static Map<Integer, String> CARD_ATTRIBUTES = new LinkedHashMap<Integer, String>() {{
        put(0x1, "地");
        put(0x2, "水");
        put(0x4, "炎");
        put(0x8, "风");
        put(0x10, "光");
        put(0x20, "暗");
        put(0x40, "神");
    }};
    static Map<Integer, String> LINK_MARKERS = new LinkedHashMap<Integer, String>() {{
        put(0x40, "[↖]");
        put(0x80, "[↑]");
        put(0x100, "[↗]");
        put(0x8, "[←]");
        put(0x20, "[→]");
        put(0x1, "[↙]");
        put(0x2, "[↓]");
        put(0x4, "[↘]");
    }};

    public static String getPreview(CardPreviewModel model) {
        if (model == null) {
            return "";
        }
        String result = CacheUtil.PreviewCache.get(model.getName());
        if (result != null) {
            return result;
        }

        StringBuilder sb = new StringBuilder();

        // 卡名
        sb.append("[");
        sb.append(model.getName());
        sb.append("]\n");

        // 种类
        mapAppend(CARD_TYPES, model.getType(), "/", sb);

        // 怪兽资料
        if ((model.getType() & MONSTER_TYPE) != 0) {
            // 等阶link
            if ((model.getType() & LINK_TYPE) != 0) {
                sb.append(" Link-");
                sb.append(model.getLevel());
            } else {
                sb.append(" ★");
                sb.append(model.getLevel() & 0xffff);
            }

            // 属性/种族
            sb.append(" ");
            mapAppend(CARD_ATTRIBUTES, model.getAttribute(), "&", sb);
            sb.append("/");
            mapAppend(CARD_RACES, model.getRace(), "&", sb);
            sb.append(" ");

            // ATK/DEF
            if (model.getAtk() < 0) {
                sb.append(" ");
            } else {
                sb.append(model.getAtk());
            }
            sb.append("/");
            if ((model.getType() & LINK_TYPE) != 0) {
                mapAppend(LINK_MARKERS, model.getDef(), "", sb);
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
        CacheUtil.PreviewCache.put(model.getName(), result);
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
