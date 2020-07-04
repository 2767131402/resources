package com.zhenglei.excel.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Test11 {
    public static void main(String[] args) {
        List<String> headList = new ArrayList<>();
        headList.add("ID");
        headList.add("所属院区");
        headList.add("所属科室");
        ExportExcel ee = new ExportExcel("设备清单", headList);
        for (int i = 0; i < 3; i++) {
            Row row = ee.addRow();
            ee.addCell(row, 0, "ID" + i);
            ee.addCell(row, 1, "所属院区" + i);
            ee.addCell(row, 2, "所属科室" + i);
        }

        //设置第二个头
        //设置第二个头
        Map<String, CellStyle> styles = ee.getStyles();
        Row row = ee.addRow();
        Cell titleCell1 = row.createCell(0);
        titleCell1.setCellStyle(styles.get("header"));
        titleCell1.setCellValue("A");
        Cell titleCell2 = row.createCell(1);
        titleCell2.setCellStyle(styles.get("header"));
        titleCell2.setCellValue("B");
        Cell titleCell3 = row.createCell(2);
        titleCell3.setCellStyle(styles.get("header"));
        titleCell3.setCellValue("C");
        Cell titleCell4 = row.createCell(3);
        titleCell4.setCellStyle(styles.get("header"));
        titleCell4.setCellValue("D");
        Cell titleCell5 = row.createCell(4);
        titleCell5.setCellStyle(styles.get("header"));
        titleCell5.setCellValue("E");
        //设置第二个头的数据
        for (int i = 0; i < 4; i++) {
            Row r = ee.addRow();
            ee.addCell(r, 0, "A" + i);
            ee.addCell(r, 1, "B" + i);
            ee.addCell(r, 2, "C" + i);
            ee.addCell(r, 3, "D" + i);
            ee.addCell(r, 4, "E" + i);
        }
        try {
            ee.writeFile("a.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
