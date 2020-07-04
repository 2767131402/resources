package com.zhenglei.excel.utils;

import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) throws Throwable {
		main2();
	}

	/**
	 * 导出模板
	 */
	public static void main0() throws Throwable {

		main2();
		
	}
		/**
		 * 导出测试
		 */
		public static void main1() throws Throwable {
			
			List<String> headerList = Lists.newArrayList();
			headerList.add("id");
			headerList.add("name");
			
			List<Student> list = new ArrayList<>();
			Student s1 = new Student("1","111");
			Student s2 = new Student("2","222");
			Student s3 = new Student("3","333");
			list.add(s1);
			list.add(s2);
			list.add(s3);
			
			ExportExcel ee = new ExportExcel("表格标题", Student.class);
			ee.setDataList(list).writeFile("D:\\export1.xlsx").dispose();
			System.err.println("OK............");
		}
		/**
		 * 导出测试
		 */
		public static void main2() throws Throwable {
			
			List<String> headerList = Lists.newArrayList();
			for (int i = 0; i <= 20; i++) {
				headerList.add("数据"+i);
			}
			
			ExportExcel ee = new ExportExcel("表格标题", headerList);
			
			for (int i = 0; i < 10000; i++) {
				Row row = ee.addRow();
				for (int j = 0; j <= 20; j++) {
					ee.addCell(row, j, "测试"+i);
				}
			}
			
			ee.writeFile("D:\\export.xls");
			
			ee.dispose();
			System.err.println("OK............");
		}

}
