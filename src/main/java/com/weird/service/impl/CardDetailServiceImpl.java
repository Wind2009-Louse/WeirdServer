package com.weird.service.impl;

import com.weird.mapper.card.CardDetailMapper;
import com.weird.model.CardDetailModel;
import com.weird.service.CardDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡片详细Service
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Service
@Slf4j
public class CardDetailServiceImpl implements CardDetailService {
    @Autowired
    CardDetailMapper cardDetailMapper;

    Map<String, String> cache = new HashMap<>();

    final Map<Integer, String> cardTypes = new HashMap<Integer, String>() {{
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
    final Map<Integer, String> cardRaces = new HashMap<Integer, String>() {{
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
    final Map<Integer, String> cardAttributes = new HashMap<Integer, String>() {{
        put(0x1, "地");
        put(0x2, "水");
        put(0x4, "炎");
        put(0x8, "风");
        put(0x10, "光");
        put(0x20, "暗");
        put(0x40, "神");
    }};
    final Map<Integer, String> linkMarkers = new HashMap<Integer, String>() {{
        put(0x40, "[↖]");
        put(0x80, "[↑]");
        put(0x100, "[↗]");
        put(0x8, "[←]");
        put(0x20, "[→]");
        put(0x1, "[↙]");
        put(0x2, "[↓]");
        put(0x4, "[↘]");
    }};

    /**
     * 根据卡名返回描述
     *
     * @param name 卡名
     * @return 卡片描述
     */
    @Override
    public String selectDetailsByName(String name) {
        String result = cache.get(name);
        if (result != null) {
            return result;
        }
        List<CardDetailModel> list = cardDetailMapper.getDetailByName(name);
        if (list == null || list.size() <= 0) {
            result = "";
        } else {
            CardDetailModel model = list.get(0);
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
                    if (model.getDef() < 0){
                        sb.append("?");
                    } else {
                        sb.append(model.getDef());
                    }
                }
            }
            sb.append("\n");
            sb.append(model.getDesc());

            result = sb.toString();
        }
        cache.put(name, result);
        return result;
    }

    private void mapAppend(Map<Integer, String> map, int target, String joiner, StringBuilder sb) {
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
