package com.weird.model.enums;

import com.weird.utils.OperationException;

import static com.weird.model.enums.AdventureEnumConst.*;

/**
 * 冒险设定
 *
 * @author Nidhogg
 * @date 2021.10.18
 */
public enum AdventureEnum {
    /**
     * 起点
     */
    STARTER(0, "起点", "起点", SCORPION),
    SECRET_WAY(1, "通向财宝的隐藏通路", "隐藏通路|隐藏通道|左腕的代偿|左腕的代价|王家的财宝|欲望的幻象", "拥有多种可能性的通路！"),
    ZERA(2, "大魔王 魔杰拉", "魔杰拉|暗杰拉|黑杰拉", NORMAL_DESC + FIELD_CROSS_WAY),
    LIGHT_ZERA(3, "大天使 杰拉特", "光杰拉|代行|天空的使者|杰拉迪亚斯", NORMAL_DESC + FIELD_CROSS_WAY),
    LIGHTLORD(4, "光道圣女 密涅瓦", "光道少女|米涅瓦", NORMAL_DESC + FIELD_CROSS_WAY),
    DARKWORLD(5, "暗黑界的导师 塞鲁利", "暗黑界", NORMAL_DESC + FIELD_CROSS_WAY),

    CRYSTAL(6, "正义的同伴 约翰侠", "宝玉兽", NORMAL_DESC + FIELD_JUSTICE),
    ARCHFIEND(7, "灭绝国王恶魔", "国王恶魔", "接受灭绝国王恶魔的六面骰子吧！"),
    SCIENTIST(8, "魔导科学家", "魔科", "魔导科学家会带来怎样的科技呢……"),
    EXODIA(9, "守护神 艾克佐德", "艾克佐迪亚|老I|老艾|岩石|魔救", NORMAL_DESC + FIELD_WASTELAND),
    SPARTA(10, "斯巴达咒术师", "剑斗兽", NORMAL_DESC + FIELD_WASTELAND),
    CHAOS(11, "圣女 贞德", "圣女贞德|救祓少女", NORMAL_DESC + FIELD_WASTELAND),

    HALF_SCORPION(12, "黑蝎盗掘团", "黑蝎", SCORPION),
    GRAVE_KEEPER(13, "鬼计妖魔 阿鲁卡德", "A少", "不给糖就捣鬼计！"),
    SKULL(14, "白骨王子", "白骨", NORMAL_DESC + FIELD_GRAVE),
    FANDORA(15, "太空翻车鱼", "翻车鱼|空牙|飞船|飞艇|飞翼犀牛|外星人母后|死域海的灯塔|玄化|Boss|boss|BOSS|碑像天使|水晶化身|太空旋风", "神秘飞船的目的地是——" + FIELD_CELESTIA),
    KOZAKY(16, "平庸鬼", "自爆", "看，平庸鬼！" + FIELD_GRAVE),
    SHIP(17, "灵魂护送船", "灵魂船|幽灵船|传说的渔人|海皇子 尼普深渊王|熔岩裁决王|炎王兽 甘尼许|忘却的海底神殿|舍利军贯|军舰|真海皇 特里冬|炎王兽 大鹏不死鸟", "一艘被恶灵占领的破船……" + FIELD_SEA),

    RED_EYE(18, "正义的同伴 城之内侠", "正义的同伴 成之内侠|真红眼|凡骨", NORMAL_DESC + FIELD_JUSTICE),
    GOBLIN(19, "哥布林王", "哥布林王", "看上去是彬彬有礼那种？"),
    MOJA(20, "毛兽攻击者", "毛扎王", NORMAL_DESC + FIELD_FOREST),
    SYLVAN(21, "森罗的姬芽君 幼芽", "森罗的公主|森罗公主", NORMAL_DESC + FIELD_FOREST),
    EARTHBOUND(22, "地缚大神官", "地缚神官|昆虫", NORMAL_DESC + FIELD_FOREST),
    SHOOTER(23, "潜行狙击手", "千星|骰子|枪手", "吃我这枪！"),
    ;

    private final int index;
    private final String name;
    private final String desc;
    private final String searchArg;

    AdventureEnum(int index, String name, String arg, String desc) {
        this.name = name;
        this.index = index;
        this.searchArg = arg;
        this.desc = desc;
    }

    static public AdventureEnum getDistinctByName(String name) throws OperationException {
        AdventureEnum result = null;
        for (AdventureEnum target : AdventureEnum.values()) {
            if (target.name.equals(name)) {
                return target;
            }
            if (target.name.contains(name) || target.searchArg.contains(name)) {
                if (result == null) {
                    result = target;
                } else {
                    throw new OperationException("找到多于一个冒险地点，请修改关键词！");
                }
            }
        }
        if (result == null) {
            throw new OperationException("找不到该冒险地点！");
        } else {
            return result;
        }
    }

    static public AdventureEnum getDistinctByIndex(int index) {
        for (AdventureEnum target : AdventureEnum.values()) {
            if (target.index == index) {
                return target;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public String getInfo() {
        return String.format("[%s]\n%s", name, desc);
    }
}
