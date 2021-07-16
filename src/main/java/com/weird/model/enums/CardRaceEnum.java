package com.weird.model.enums;

/**
 * 卡片种族
 *
 * @author Nidhogg
 * @date 2021.7.16
 */
public enum CardRaceEnum {
    /**
     * 怪兽
     */
    WARRIOR("战士族", 0x1),
    CASTER("魔法师族", 0x2),
    FAIRY("天使族", 0x4),
    DEMON("恶魔族", 0x8),
    UNDEAD("不死族", 0x10),
    MACHINE("机械族", 0x20),
    AQUA("水族", 0x40),
    FLAME("炎族",0x80),
    ROCK("岩石族", 0x100),
    BIRD("鸟兽族", 0x200),
    PLANT("植物族", 0x400),
    INSECT("昆虫族", 0x800),
    THUNDER("雷族", 0x1000),
    DRAGON("龙族", 0x2000),
    BEAST("兽族", 0x4000),
    BEAST_WARRIOR("兽战士族", 0x8000),
    DINOSAUR("恐龙族", 0x10000),
    FISH("鱼族", 0x20000),
    SEA_SERPENT("海龙族", 0x40000),
    REPTILE("爬虫类族", 0x80000),
    PSYCHO("念动力族", 0x100000),
    FB("幻神兽族", 0x200000),
    CREATOR("创造神族", 0x400000),
    WYRM("幻龙族", 0x800000),
    CYBERSE("电子界族", 0x1000000),
    LINK("连接", 0x4000000);

    private final String name;
    private final int value;

    CardRaceEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static CardRaceEnum getByName(String name) {
        for (CardRaceEnum data : CardRaceEnum.values()) {
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }
}
