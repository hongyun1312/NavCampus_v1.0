package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 路网边实体。
 * 存储节点间的连接关系、距离、通行难度等信息。
 */
@Data
@TableName("campus_road_edge")
public class CampusRoadEdge {
    @TableId(type = IdType.AUTO)
    private Long edgeId;

    private Long startNodeId;
    private Long endNodeId;

    private BigDecimal distance;
    private Boolean isOneWay;
    private Integer difficulty;
    private Integer status;
}
