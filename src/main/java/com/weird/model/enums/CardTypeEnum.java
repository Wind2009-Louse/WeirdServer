package com.weird.model.enums;

/**
 * 卡片种类
 *
 * @author Nidhogg
 * @date 2021.7.16
 */
public enum CardTypeEnum {
    /**
     * 怪兽
     */
    MONSTER("怪兽", 0x1),
    SPELL("魔法", 0x2),
    TRAP("陷阱", 0x4),
    NORMAL("通常", 0x10),
    EFFECT("效果", 0x20),
    EFFECT_M("效果怪兽", 0x20),
    FUSION("融合", 0x40),
    FUSION_M("融合怪兽", 0x40),
    RITUAL("仪式",0x80),
    RITUAL_M("仪式怪兽",0x80),
    SPIRIT("灵魂", 0x200),
    SPIRIT_M("灵魂怪兽", 0x200),
    UNION("同盟", 0x400),
    UNION_M("同盟怪兽", 0x400),
    GEMINI("二重", 0x800),
    GEMINI_M("二重怪兽", 0x800),
    TUNER("调整", 0x1000),
    TUNER_M("调整怪兽", 0x1000),
    SYNCHRO("同调", 0x2000),
    SYNCHRO_M("同调怪兽", 0x2000),
    TOKEN("衍生物", 0x4000),
    QUICK("速攻", 0x10000),
    QUICK_S("速攻魔法", 0x10000),
    CONTINUOUS("永续", 0x20000),
    EQUIP("装备", 0x40000),
    EQUIP_S("装备魔法", 0x40000),
    FIELD("场地", 0x80000),
    FIELD_S("场地魔法", 0x80000),
    COUNTER("反击", 0x100000),
    COUNTER_T("反击陷阱", 0x100000),
    FLIP("反转", 0x200000),
    FLIP_M("反转怪兽", 0x200000),
    TOON("卡通", 0x400000),
    TOON_M("卡通怪兽", 0x400000),
    XYZ("超量", 0x800000),
    XYZ_M("超量怪兽", 0x800000),
    XYZ_ORIGIN("XYZ", 0x800000),
    XYZ_ORIGIN_M("XYZ怪兽", 0x800000),
    PENDULUM("灵摆", 0x1000000),
    PENDULUM_M("灵摆怪兽", 0x1000000),
    LINK("连接", 0x4000000),
    LINK_M("连接怪兽", 0x4000000),

    NORMAL_MONSTER("通常怪兽", 0x10+0x1),
    NORMAL_SPELL("通常魔法", 0x10+0x2),
    NORMAL_TRAP("通常陷阱", 0x10+0x4),
    CONTINUOUS_SPELL("永续魔法", 0x20000+0x2),
    CONTINUOUS_TRAP("永续陷阱", 0x20000+0x4),
    RITUAL_SPELL("仪式魔法",0x80+0x2);

    private final String name;
    private final int value;

    CardTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static CardTypeEnum getByName(String name) {
        for (CardTypeEnum data : CardTypeEnum.values()) {
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }
}
