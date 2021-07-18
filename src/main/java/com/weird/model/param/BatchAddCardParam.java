package com.weird.model.param;

import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量导入卡片参数
 *
 * @author Nidhogg
 * @date 2020.9.17
 */
@Data
public class BatchAddCardParam extends UserCheckParam implements Serializable, Trimable {
    /**
     * 卡包名
     */
    String packageName;

    /**
     * n卡列表
     */
    List<String> nList;

    /**
     * r卡列表
     */
    List<String> rList;

    /**
     * sr列表
     */
    List<String> srList;

    /**
     * ur列表
     */
    List<String> urList;

    /**
     * hr列表
     */
    List<String> hrList;

    /**
     * gr列表
     */
    List<String> grList;


    /**
     * ser列表
     */
    List<String> serList;
}
