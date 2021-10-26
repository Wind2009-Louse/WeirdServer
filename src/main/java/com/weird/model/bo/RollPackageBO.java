package com.weird.model.bo;

import com.weird.model.dto.CardListDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 抽卡时，获取的卡包卡片信息
 *
 * @author Nidhogg
 * @date 2021.10.26
 */
@Data
public class RollPackageBO implements Serializable {
    /**
     * N卡列表
     */
    List<CardListDTO> normalList;
    /**
     * R卡列表
     */
    List<CardListDTO> rareList;
    /**
     * 闪卡（SR、UR、HR）列表
     */
    List<CardListDTO> awardList;
    /**
     * 特殊卡（GR、SER）列表
     */
    List<CardListDTO> spList;

    public RollPackageBO() {
        normalList = new LinkedList<>();
        rareList = new LinkedList<>();
        awardList = new LinkedList<>();
        spList = new LinkedList<>();
    }
}
