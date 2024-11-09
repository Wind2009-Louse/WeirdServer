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
    WARRIOR("战士", 0x1),
    WARRIOR_R("战士族", 0x1),
    CASTER("魔法师", 0x2),
    CASTER_R("魔法师族", 0x2),
    FAIRY("天使", 0x4),
    FAIRY_R("天使族", 0x4),
    DEMON("恶魔", 0x8),
    DEMON_R("恶魔族", 0x8),
    UNDEAD("不死", 0x10),
    UNDEAD_R("不死族", 0x10),
    MACHINE("机械", 0x20),
    MACHINE_R("机械族", 0x20),
    AQUA("水", 0x40),
    AQUA_R("水族", 0x40),
    FLAME("炎",0x80),
    FLAME_R("炎族",0x80),
    ROCK("岩石", 0x100),
    ROCK_R("岩石族", 0x100),
    BIRD("鸟兽", 0x200),
    BIRD_R("鸟兽族", 0x200),
    PLANT("植物", 0x400),
    PLANT_R("植物族", 0x400),
    INSECT("昆虫", 0x800),
    INSECT_R("昆虫族", 0x800),
    THUNDER("雷", 0x1000),
    THUNDER_R("雷族", 0x1000),
    DRAGON("龙", 0x2000),
    DRAGON_R("龙族", 0x2000),
    BEAST("兽", 0x4000),
    BEAST_R("兽族", 0x4000),
    BEAST_WARRIOR("兽战士", 0x8000),
    BEAST_WARRIOR_R("兽战士族", 0x8000),
    DINOSAUR("恐龙", 0x10000),
    DINOSAUR_R("恐龙族", 0x10000),
    FISH("鱼", 0x20000),
    FISH_R("鱼族", 0x20000),
    SEA_SERPENT("海龙", 0x40000),
    SEA_SERPENT_R("海龙族", 0x40000),
    REPTILE("爬虫类", 0x80000),
    REPTILE_R("爬虫类族", 0x80000),
    PSYCHO("念动力", 0x100000),
    PSYCHO_R("念动力族", 0x100000),
    FB("幻神兽", 0x200000),
    FB_R("幻神兽族", 0x200000),
    CREATOR("创造神", 0x400000),
    CREATOR_R("创造神族", 0x400000),
    WYRM("幻龙", 0x800000),
    WYRM_R("幻龙族", 0x800000),
    CYBERSE("电子界", 0x1000000),
    CYBERSE_R("电子界族", 0x1000000),
    ILLUSION("幻想魔", 0x1000000),
    ILLUSION_R("幻想魔族", 0x2000000);

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
