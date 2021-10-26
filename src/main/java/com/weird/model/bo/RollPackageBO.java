package com.weird.model.bo;

import com.weird.model.dto.CardListDTO;
import com.weird.utils.ResponseException;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    /**
     * 从N卡列表随机抽取两个
     *
     * @return
     */
    public List<CardListDTO> getRandomResultFromNormal() throws ResponseException {
        final int size = normalList.size();
        if (size < 2) {
            throw new ResponseException("N卡数量不足！");
        }

        Random rd = new Random();
        int normalIndex1 = rd.nextInt(size);
        int normalIndex2 = rd.nextInt(size);
        while (normalIndex1 == normalIndex2) {
            normalIndex2 = rd.nextInt(size);
        }
        return Arrays.asList(normalList.get(normalIndex1), normalList.get(normalIndex2));
    }
}
