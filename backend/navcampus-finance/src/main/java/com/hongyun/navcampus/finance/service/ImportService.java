package com.hongyun.navcampus.finance.service;

import com.hongyun.navcampus.finance.entity.Account;
import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.finance.entity.Record;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.finance.mapper.AccountMapper;
import com.hongyun.navcampus.finance.mapper.CategoryMapper;
import com.hongyun.navcampus.finance.mapper.RecordMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * 批量导入服务。
 * 支持 Excel（Apache POI）与 CSV 流水导入，包含数据去重与基本校验。
 */
@Service
public class ImportService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    private User userByUsername(String username) {
        return userMapper.findByUsername(username).orElseThrow();
    }

    /**
     * 导入 Excel 文件。
     * 期望列：时间(yyyy-MM-dd HH:mm)、类型(INCOME/EXPENSE/TRANSFER)、分类名称、账户名称、金额、备注
     * @return 成功导入条数
     */
    public int importExcel(MultipartFile file, String username) throws Exception {
        User user = userByUsername(username);
        Workbook wb = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = wb.getSheetAt(0);
        Set<String> existing = existingKeys(user);
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String timeStr = readTimeCell(row.getCell(0));
            String typeStr = readStringCell(row.getCell(1));
            String categoryName = readStringCell(row.getCell(2));
            String accountName = readStringCell(row.getCell(3));
            BigDecimal amount = readAmountCell(row.getCell(4));
            String remark = readStringCell(row.getCell(5));
            LocalDateTime time = parseTimeFlexible(timeStr);
            Category category = categoryMapper.findByUserId(user.getId()).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName)).findFirst().orElse(null);
            Account account = accountMapper.findByUserId(user.getId()).stream()
                    .filter(a -> a.getName().equalsIgnoreCase(accountName)).findFirst().orElseThrow();
            String key = dedupKey(user, amount, typeStr, time, category, account);
            if (existing.contains(key)) continue;
            Record r = new Record();
            r.setUser(user);
            r.setAmount(amount);
            r.setType(Record.RecordType.valueOf(typeStr));
            r.setTime(time);
            r.setCategory(category);
            r.setAccount(account);
            r.setRemark(remark);
            recordMapper.save(r);
            existing.add(key);
            count++;
        }
        return count;
    }

    /**
     * 导入 CSV 文件。
     * 期望列：时间,类型,分类名称,账户名称,金额,备注
     * @return 成功导入条数
     */
    public int importCsv(MultipartFile file, String username) throws Exception {
        User user = userByUsername(username);
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        Set<String> existing = existingKeys(user);
        String line;
        int count = 0;
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            if (arr.length < 5) continue;
            LocalDateTime time = LocalDateTime.parse(arr[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String typeStr = arr[1];
            String categoryName = arr[2];
            String accountName = arr[3];
            BigDecimal amount = new BigDecimal(arr[4]).setScale(2, java.math.RoundingMode.HALF_UP);
            String remark = arr.length > 5 ? arr[5] : null;
            Category category = categoryMapper.findByUserId(user.getId()).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName)).findFirst().orElse(null);
            Account account = accountMapper.findByUserId(user.getId()).stream()
                    .filter(a -> a.getName().equalsIgnoreCase(accountName)).findFirst().orElseThrow();
            String key = dedupKey(user, amount, typeStr, time, category, account);
            if (existing.contains(key)) continue;
            Record r = new Record();
            r.setUser(user);
            r.setAmount(amount);
            r.setType(Record.RecordType.valueOf(typeStr));
            r.setTime(time);
            r.setCategory(category);
            r.setAccount(account);
            r.setRemark(remark);
            recordMapper.save(r);
            existing.add(key);
            count++;
        }
        return count;
    }

    /**
     * 生成已存在记录的去重键集合。
     */
    private Set<String> existingKeys(User user) {
        Set<String> keys = new HashSet<>();
        for (Record r : recordMapper.findByUserId(user.getId())) {
            String t = r.getType().name();
            String c = r.getCategoryId() != null ? categoryMapper.selectById(r.getCategoryId()).getName() : "NULL";
            String a = r.getAccountId() != null ? accountMapper.selectById(r.getAccountId()).getName() : "NULL";
            String key = user.getId() + "|" + r.getAmount().setScale(2, java.math.RoundingMode.HALF_UP) + "|" + t + "|" + r.getTime() + "|" + c + "|" + a;
            keys.add(key);
        }
        return keys;
    }

    /**
     * 生成去重键。
     */
    private String dedupKey(User user, java.math.BigDecimal amount, String type, LocalDateTime time, Category category, Account account) {
        String c = category != null ? category.getName() : "NULL";
        return user.getId() + "|" + amount.setScale(2, java.math.RoundingMode.HALF_UP) + "|" + type + "|" + time + "|" + c + "|" + account.getName();
    }

    // Helpers for robust Excel parsing
    private final DataFormatter formatter = new DataFormatter();
    private final DateTimeFormatter strictFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter altFmt = DateTimeFormatter.ofPattern("yyyy/M/d H:mm");

    private String readStringCell(Cell cell) {
        if (cell == null) return null;
        return formatter.formatCellValue(cell).trim();
    }

    private BigDecimal readAmountCell(Cell cell) {
        if (cell == null) return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        if (cell.getCellType() == CellType.NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue()).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        String s = formatter.formatCellValue(cell).trim();
        if (s.isEmpty()) return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        return new BigDecimal(s).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private String readTimeCell(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            java.util.Date d = cell.getDateCellValue();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(d);
        }
        return formatter.formatCellValue(cell).trim();
    }

    private LocalDateTime parseTimeFlexible(String s) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException("时间不能为空");
        try {
            return LocalDateTime.parse(s, strictFmt);
        } catch (DateTimeParseException e) {
            // try alternate common format
            return LocalDateTime.parse(s.replace('.', '-').replace('/', '-'), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }
}
