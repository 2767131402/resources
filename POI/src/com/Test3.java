package com;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Test3 {
	public static void main(String[] args) throws Exception {
		ExportExcel4MultiSheet e = new ExportExcel4MultiSheet();
		List<String> headerListA = new ArrayList<>();
		headerListA.add("1");
		headerListA.add("2");
		headerListA.add("3");
		Sheet creatSheetA = e.creatSheet("a", "aaa", headerListA);
		headerListA.add("4");
		Sheet creatSheetB = e.creatSheet("b", "bbb", headerListA);
		headerListA.add("5");
		Sheet creatSheetC = e.creatSheet("c", "ccc", headerListA);
		for (int i = 0; i < 3; i++) {
			Row row = e.addRow(creatSheetA, i+2);
			for (int j = 0; j < 3; j++) {
				e.addCell(creatSheetA, row, j, "1");
			}
		}
		for (int i = 0; i < 3; i++) {
			Row row = e.addRow(creatSheetB, i+2);
			for (int j = 0; j < 4; j++) {
				e.addCell(creatSheetB, row, j, "1");
			}
		}
		for (int i = 0; i < 3; i++) {
			Row row = e.addRow(creatSheetC, i+2);
			for (int j = 0; j < 5; j++) {
				e.addCell(creatSheetC, row, j, "1");
			}
		}
		
		OutputStream os = new FileOutputStream("D:\\aaa.xlsx");
		e.write(os).dispose();
		
	}
}
