package com.weird.model.param;

import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 批量更改用户卡片持有数量
 *
 * @author Nidhogg
 * @date 2020.9.23
 */
@Data
public class BatchUpdateUserCardParam extends UserCheckParam implements Serializable, Trimable {
    /**
     * 操作目标
     */
    String target;

    /**
     * 卡片持有数量
     */
    Map<String, Integer> counts;
}
