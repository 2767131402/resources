package com;

import java.io.File;
import java.util.List;

public class Test2 {

		/**
		 * 导出测试
		 */
		public static void main(String[] args) throws Throwable {
			
			ImportExcel ee = new ImportExcel(new File("D:\\export.xlsx"),1);
			List<Student> dataList = ee.getDataList(Student.class);
			System.err.println(dataList);
			
	}

}
