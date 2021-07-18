package com.weird.model.param;

import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 卡包排序参数
 *
 * @author Nidhogg
 * @date 2020.10.5
 */
@Data
public class PackageSortParam extends UserCheckParam implements Serializable, Trimable {
    /**
     * 新的卡包顺序
     */
    List<Integer> packageIndexList;
}
