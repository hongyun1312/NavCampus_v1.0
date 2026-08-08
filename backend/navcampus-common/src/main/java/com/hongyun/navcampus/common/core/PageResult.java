package com.hongyun.navcampus.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页查询结果包装类。
 *
 * @param <T> 列表元素类型
 */
@Data
@Schema(description = "分页查询结果")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "当前页码")
    private long current;

    @Schema(description = "每页条数")
    private long size;

    public PageResult() {}

    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }
}