package com.hongyun.navcampus.finance.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.finance.converter.VoConverter;
import com.hongyun.navcampus.finance.entity.Account;
import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.finance.entity.Record;
import com.hongyun.navcampus.finance.dto.RecordRequest;
import com.hongyun.navcampus.finance.mapper.AccountMapper;
import com.hongyun.navcampus.finance.mapper.CategoryMapper;
import com.hongyun.navcampus.finance.service.RecordService;
import com.hongyun.navcampus.finance.vo.RecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@Tag(name = "收支记录", description = "记录查询、创建与删除")
public class RecordController {
    @Autowired
    private RecordService recordService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "查询当前用户全部记录")
    public R<List<RecordVO>> list() {
        return R.ok(VoConverter.toRecordVOList(recordService.getAllRecords()));
    }

    @GetMapping("/range")
    @Operation(summary = "按时间范围查询记录")
    public R<List<RecordVO>> listByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return R.ok(VoConverter.toRecordVOList(recordService.getRecordsByDateRange(start, end)));
    }

    @PostMapping
    @Operation(summary = "创建记录")
    public R<RecordVO> create(@Valid @RequestBody RecordRequest req) {
        Record r = new Record();
        r.setAmount(req.getAmount());
        r.setType(Record.RecordType.valueOf(req.getType()));
        r.setTime(LocalDateTime.parse(req.getTime()));
        Account account = accountMapper.findById(req.getAccountId()).orElseThrow();
        r.setAccount(account);
        if (req.getCategoryId() != null) {
            Category c = categoryMapper.findById(req.getCategoryId()).orElse(null);
            r.setCategory(c);
        }
        if (req.getTargetAccountId() != null) {
            Account target = accountMapper.findById(req.getTargetAccountId()).orElseThrow();
            r.setTargetAccount(target);
        }
        r.setRemark(req.getRemark());
        r.setLocation(req.getLocation());
        return R.ok(VoConverter.toRecordVO(recordService.createRecord(r)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除记录")
    public R<Void> delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return R.ok();
    }

    @DeleteMapping
    @Operation(summary = "删除所有记录")
    public R<Void> deleteAll() {
        recordService.deleteAllRecords();
        return R.ok();
    }
}
