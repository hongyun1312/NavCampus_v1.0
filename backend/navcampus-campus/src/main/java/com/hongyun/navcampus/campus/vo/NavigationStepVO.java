package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 单步导航指令。
 * 表示路径中一段的转向方向、距离和起止节点信息。
 */
@Data
@Schema(description = "单步导航指令")
public class NavigationStepVO {

    @Schema(description = "导航指令文本，如\"直行50米后左转\"", example = "直行 50.00 米")
    private String instruction;

    @Schema(description = "转向方向枚举：STRAIGHT/SLIGHT_LEFT/LEFT/SHARP_LEFT/UTURN/SHARP_RIGHT/RIGHT/SLIGHT_RIGHT")
    private String direction;

    @Schema(description = "本段距离（模型单位）")
    private BigDecimal distance;

    @Schema(description = "本段起点节点ID")
    private Long fromNodeId;

    @Schema(description = "本段起点节点名称")
    private String fromNodeName;

    @Schema(description = "本段终点节点ID")
    private Long toNodeId;

    @Schema(description = "本段终点节点名称")
    private String toNodeName;

    @Schema(description = "转向角度（度），0=直行，正值=右转，负值=左转")
    private Double turnAngle;
}
