package com.hongyun.navcampus.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 路网节点实体。
 * 存储校园3D模型中的路径节点坐标，支持地标节点与普通路径节点。
 */
@Data
@TableName("campus_road_node")
public class CampusRoadNode {
    @TableId(type = IdType.AUTO)
    private Long nodeId;

    private String nodeName;

    private BigDecimal xCoordinate;
    private BigDecimal yCoordinate;
    private BigDecimal zCoordinate;

    private Boolean isLandmark;
    private Integer status;
}
