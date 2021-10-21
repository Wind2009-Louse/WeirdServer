package com.weird.model.param;

import lombok.Data;

/**
 * 检查卡组是否可用
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class DeckUsableParam extends UserCheckParam {
    /**
     * ydk文本
     */
    String ydk;
}
