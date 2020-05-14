package com.zhenglei.utils.quartz_manager;

public class TestJob {
	public static void main(String[] args) throws InterruptedException {
		boolean b = addJob("123");
		boolean bb = addJob("456");
		QuartzManager a = new QuartzManager();
		a.getAll();
	}
	
	public static boolean addJob(String name) {
		QuartzManager a = new QuartzManager();
		boolean add = a.add(name, Jobs.class, 1, "000");
		return add;
	}
}
