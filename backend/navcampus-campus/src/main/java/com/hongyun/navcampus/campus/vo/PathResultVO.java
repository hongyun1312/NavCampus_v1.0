package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路径规划结果。
 * 包含路径节点列表、总距离、预计步行时间、转向导航指令等。
 */
@Data
@Schema(description = "路径规划结果")
public class PathResultVO {

    @Schema(description = "是否找到路径")
    private Boolean found;

    @Schema(description = "路径节点列表（按行进顺序）")
    private List<PathNodeVO> path;

    @Schema(description = "总距离（模型单位，约等于米）")
    private BigDecimal totalDistance;

    @Schema(description = "预计步行时间（秒），按 1.4m/s 步行速度计算")
    private Integer estimatedTime;

    @Schema(description = "路径节点数量")
    private Integer nodeCount;

    @Schema(description = "转向导航指令列表，按行进顺序排列")
    private List<NavigationStepVO> steps;

    /**
     * 路径中的单个节点。
     */
    @Data
    @Schema(description = "路径节点")
    public static class PathNodeVO {
        private Long nodeId;
        private String nodeName;
        private BigDecimal x;
        private BigDecimal y;
        private BigDecimal z;
    }
}
