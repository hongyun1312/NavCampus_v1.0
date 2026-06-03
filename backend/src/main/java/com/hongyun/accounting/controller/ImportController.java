package com.hongyun.accounting.controller;

import com.hongyun.accounting.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Map;

/**
 * 导入控制器。
 * 提供 Excel/CSV 文件的批量导入接口。
 */
@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {
    @Autowired
    private ImportService importService;

    /**
     * 导入 Excel 文件。
     */
    @PostMapping("/excel")
    public Object importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int n = importService.importExcel(file, ud.getUsername());
        return Map.of("imported", n);
    }

    /**
     * 导入 CSV 文件。
     */
    @PostMapping("/csv")
    public Object importCsv(@RequestParam("file") MultipartFile file) throws Exception {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int n = importService.importCsv(file, ud.getUsername());
        return Map.of("imported", n);
    }

    /**
     * 下载导入示例 Excel。
     * 列与格式严格符合导入服务的预期：
     * 时间(yyyy-MM-dd HH:mm)、类型(INCOME/EXPENSE/TRANSFER)、分类名称、账户名称、金额、备注
     */
    @GetMapping("/sample")
    public void downloadSample(HttpServletResponse response) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("导入示例");
        // Header
        Row h = sheet.createRow(0);
        h.createCell(0).setCellValue("时间(yyyy-MM-dd HH:mm)");
        h.createCell(1).setCellValue("类型(INCOME/EXPENSE/TRANSFER)");
        h.createCell(2).setCellValue("分类名称");
        h.createCell(3).setCellValue("账户名称");
        h.createCell(4).setCellValue("金额");
        h.createCell(5).setCellValue("备注");
        // Row 1
        Row r1 = sheet.createRow(1);
        r1.createCell(0).setCellValue("2025-12-01 08:30");
        r1.createCell(1).setCellValue("EXPENSE");
        r1.createCell(2).setCellValue("餐饮");
        r1.createCell(3).setCellValue("现金");
        r1.createCell(4).setCellValue(35.50);
        r1.createCell(5).setCellValue("早餐");
        // Row 2
        Row r2 = sheet.createRow(2);
        r2.createCell(0).setCellValue("2025-12-02 18:20");
        r2.createCell(1).setCellValue("INCOME");
        r2.createCell(2).setCellValue("工资");
        r2.createCell(3).setCellValue("银行卡");
        r2.createCell(4).setCellValue(5000.00);
        r2.createCell(5).setCellValue("十二月工资");
        // Row 3
        Row r3 = sheet.createRow(3);
        r3.createCell(0).setCellValue("2025-12-03 12:00");
        r3.createCell(1).setCellValue("EXPENSE");
        r3.createCell(2).setCellValue("交通");
        r3.createCell(3).setCellValue("微信");
        r3.createCell(4).setCellValue(5.00);
        r3.createCell(5).setCellValue("公交");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"import-sample.xlsx\"");
        wb.write(response.getOutputStream());
        wb.close();
    }
}
