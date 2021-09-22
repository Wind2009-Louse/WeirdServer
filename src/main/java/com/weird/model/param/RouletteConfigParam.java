package com.weird.model.param;

import com.weird.interfaces.Trimable;
import com.weird.model.dto.RouletteConfigDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 修改转盘配置的参数
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Data
public class RouletteConfigParam extends UserCheckParam implements Serializable, Trimable {
    List<RouletteConfigDTO> list;
}
