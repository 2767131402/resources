package com.zhenglei.excel.utils;

public class Student {
	@ExcelField(title = "id", type = 0, align = 2, sort = 20)
	private String id;
	@ExcelField(title = "name", with=30, type = 0, align = 2, sort = 20)
	private String name;

	
	public Student(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + "]";
	}

}
