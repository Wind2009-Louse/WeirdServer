package com.weird.utils;

import com.weird.model.CardPreviewModel;
import com.weird.model.enums.CardTypeEnum;
import com.weird.model.param.BlurSearchParam;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
        return getPreview(model, true);
    }

    public static String getPreview(CardPreviewModel model, boolean useName) {
        if (model == null) {
            return "";
        }
        String result = CacheUtil.PreviewCache.get(model.getName());
        if (result == null) {
            StringBuilder sb = new StringBuilder();

            // 种类
            mapAppend(CARD_TYPES, model.getType(), "/", sb);
            if (model.getType() == CardTypeEnum.SPELL.getValue() || model.getType() == CardTypeEnum.TRAP.getValue()) {
                sb.append("/通常");
            }

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
                    sb.append("?");
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

            // 效果描述
            sb.append(model.getDesc());

            result = sb.toString();
            CacheUtil.PreviewCache.put(model.getName(), result);
        }

        String prefix;
        if (useName) {
            prefix = buildName(model);
        } else {
            prefix = "";
        }

        return prefix + result;
    }

    private static String buildName(CardPreviewModel model) {
        if (model == null) {
            return "\n";
        }
        return "[" + model.getName() + "]\n";
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

    /**
     * 对入参进行范围搜索的设置
     *
     * @param string       输入参数
     * @param param        参数类
     * @param eqSetter     获取相等的匹配
     * @param geListGetter >=
     * @param gListGetter  >
     * @param leListGetter <=
     * @param lListGetter  <
     * @return
     */
    public static boolean setRangeSearch(
            String string,
            BlurSearchParam param,
            BiConsumer<BlurSearchParam, Long> eqSetter,
            Function<BlurSearchParam, List<Long>> geListGetter,
            Function<BlurSearchParam, List<Long>> gListGetter,
            Function<BlurSearchParam, List<Long>> leListGetter,
            Function<BlurSearchParam, List<Long>> lListGetter) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        if (string.startsWith(">=")) {
            String filteredText = string.substring(2);
            Long result = convertStringToLongOrNull(filteredText);
            if (result != null) {
                geListGetter.apply(param).add(result);
                return true;
            }
        } else if (string.startsWith(">")) {
            String filteredText = string.substring(1);
            Long result = convertStringToLongOrNull(filteredText);
            if (result != null) {
                gListGetter.apply(param).add(result);
                return true;
            }
        } else if (string.startsWith("<=")) {
            String filteredText = string.substring(2);
            Long result = convertStringToLongOrNull(filteredText);
            if (result != null) {
                leListGetter.apply(param).add(result);
                return true;
            }
        } else if (string.startsWith("<")) {
            String filteredText = string.substring(1);
            Long result = convertStringToLongOrNull(filteredText);
            if (result != null) {
                lListGetter.apply(param).add(result);
                return true;
            }
        } else {
            String filteredText = string;
            if (string.startsWith("=")) {
                filteredText = string.substring(1);
            }
            Long result = convertStringToLongOrNull(filteredText);
            if (result != null) {
                eqSetter.accept(param, result);
                return true;
            }
        }
        return false;
    }

    private static Long convertStringToLongOrNull(String string) {
        Long result = null;
        if ("?".equals(string) || "？".equals(string)) {
            return -2L;
        }
        try {
            result = Long.parseLong(string);
        } catch (Exception e) {

        }
        return result;
    }
}
