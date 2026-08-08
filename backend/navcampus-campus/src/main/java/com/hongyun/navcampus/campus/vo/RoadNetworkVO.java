package com.hongyun.navcampus.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "路网数据")
public class RoadNetworkVO {

    @Schema(description = "节点列表")
    private List<NodeVO> nodes;

    @Schema(description = "边列表")
    private List<EdgeVO> edges;

    @Data
    @Schema(description = "路网节点")
    public static class NodeVO {
        private Long nodeId;
        private String nodeName;
        private BigDecimal x;
        private BigDecimal y;
        private BigDecimal z;
        private Boolean isLandmark;
    }

    @Data
    @Schema(description = "路网边")
    public static class EdgeVO {
        private Long edgeId;
        private Long startNodeId;
        private Long endNodeId;
        private BigDecimal distance;
        private Boolean isOneWay;
        private Integer difficulty;
    }
}
