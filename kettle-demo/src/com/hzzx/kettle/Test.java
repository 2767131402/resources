package com.hzzx.kettle;

public class Test {
	
	public static void main(String[] args) throws Exception {
		String[] params = { "1", "content", "d:\\test1.txt" };
		KettleUnit.runKtr(params, "D:\\soft\\data-integration\\work\\demo.ktr");
	}
	
}
