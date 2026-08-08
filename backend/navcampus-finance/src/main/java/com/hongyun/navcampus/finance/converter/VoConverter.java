package com.hongyun.navcampus.finance.converter;

import com.hongyun.navcampus.finance.entity.Account;
import com.hongyun.navcampus.finance.entity.Budget;
import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.finance.entity.Record;
import com.hongyun.navcampus.finance.vo.AccountVO;
import com.hongyun.navcampus.finance.vo.BudgetVO;
import com.hongyun.navcampus.finance.vo.CategoryVO;
import com.hongyun.navcampus.finance.vo.RecordVO;
import java.util.List;
import java.util.stream.Collectors;

public class VoConverter {

    public static AccountVO toAccountVO(Account a) {
        if (a == null) return null;
        AccountVO vo = new AccountVO();
        vo.setId(a.getId());
        vo.setName(a.getName());
        vo.setType(a.getType() != null ? a.getType().name() : null);
        vo.setBalance(a.getBalance());
        vo.setUserId(a.getUserId());
        vo.setIcon(a.getIcon());
        vo.setCreatedAt(a.getCreatedAt());
        return vo;
    }

    public static List<AccountVO> toAccountVOList(List<Account> list) {
        return list.stream().map(VoConverter::toAccountVO).collect(Collectors.toList());
    }

    public static BudgetVO toBudgetVO(Budget b) {
        if (b == null) return null;
        BudgetVO vo = new BudgetVO();
        vo.setId(b.getId());
        vo.setAmount(b.getAmount());
        vo.setType(b.getType() != null ? b.getType().name() : null);
        vo.setCategoryId(b.getCategoryId());
        vo.setUserId(b.getUserId());
        vo.setPeriod(b.getPeriod());
        vo.setCreatedAt(b.getCreatedAt());
        return vo;
    }

    public static List<BudgetVO> toBudgetVOList(List<Budget> list) {
        return list.stream().map(VoConverter::toBudgetVO).collect(Collectors.toList());
    }

    public static CategoryVO toCategoryVO(Category c) {
        if (c == null) return null;
        CategoryVO vo = new CategoryVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setType(c.getType() != null ? c.getType().name() : null);
        vo.setIcon(c.getIcon());
        vo.setColor(c.getColor());
        vo.setUserId(c.getUserId());
        vo.setCreatedAt(c.getCreatedAt());
        return vo;
    }

    public static List<CategoryVO> toCategoryVOList(List<Category> list) {
        return list.stream().map(VoConverter::toCategoryVO).collect(Collectors.toList());
    }

    public static RecordVO toRecordVO(Record r) {
        if (r == null) return null;
        RecordVO vo = new RecordVO();
        vo.setId(r.getId());
        vo.setAmount(r.getAmount());
        vo.setType(r.getType() != null ? r.getType().name() : null);
        vo.setTime(r.getTime());
        vo.setCategoryId(r.getCategoryId());
        vo.setAccountId(r.getAccountId());
        vo.setTargetAccountId(r.getTargetAccountId());
        vo.setRemark(r.getRemark());
        vo.setLocation(r.getLocation());
        vo.setUserId(r.getUserId());
        vo.setCreatedAt(r.getCreatedAt());
        return vo;
    }

    public static List<RecordVO> toRecordVOList(List<Record> list) {
        return list.stream().map(VoConverter::toRecordVO).collect(Collectors.toList());
    }
}
