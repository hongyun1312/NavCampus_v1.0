package com.hongyun.accounting.controller;

import com.hongyun.accounting.dto.RecordRequest;
import com.hongyun.accounting.entity.Account;
import com.hongyun.accounting.entity.Category;
import com.hongyun.accounting.entity.Record;
import com.hongyun.accounting.repository.AccountRepository;
import com.hongyun.accounting.repository.CategoryRepository;
import com.hongyun.accounting.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 记录控制器。
 * 提供记录的查询、按时间范围聚合、创建与删除接口。
 */
@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "*")
public class RecordController {
    @Autowired
    private RecordService recordService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 查询当前用户全部记录。
     */
    @GetMapping
    public List<Record> list() {
        return recordService.getAllRecords();
    }

    /**
     * 按时间范围查询记录。
     */
    @GetMapping("/range")
    public List<Record> listByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return recordService.getRecordsByDateRange(start, end);
    }

    /**
     * 创建记录。
     */
    @PostMapping
    public Record create(@RequestBody RecordRequest req) {
        Record r = new Record();
        r.setAmount(req.getAmount());
        r.setType(Record.RecordType.valueOf(req.getType()));
        r.setTime(LocalDateTime.parse(req.getTime()));
        Account account = accountRepository.findById(req.getAccountId()).orElseThrow();
        r.setAccount(account);
        if (req.getCategoryId() != null) {
            Category c = categoryRepository.findById(req.getCategoryId()).orElse(null);
            r.setCategory(c);
        }
        if (req.getTargetAccountId() != null) {
            Account target = accountRepository.findById(req.getTargetAccountId()).orElseThrow();
            r.setTargetAccount(target);
        }
        r.setRemark(req.getRemark());
        r.setLocation(req.getLocation());
        return recordService.createRecord(r);
    }

    /**
     * 删除记录。
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
    }

    /**
     * 删除所有记录。
     */
    @DeleteMapping
    public void deleteAll() {
        recordService.deleteAllRecords();
    }
}
