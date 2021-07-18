package com.weird.model.param;

import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 抽卡参数
 *
 * @author Nidhogg
 * @date 2020.9.18
 */
@Data
public class RollParam extends UserCheckParam implements Serializable, Trimable {
    /**
     * 抽卡对象
     */
    String target;

    /**
     * 卡片列表
     */
    List<List<String>> cards;
}
