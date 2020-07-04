package com.zhenglei.utils.email;

public class Test {
    public static void main(String[] args) {
        EmailUtils.EmailData data = new EmailUtils.EmailData();
        data.setSubject("123");
        data.setContent("洪湖");
        EmailUtils.sendText("2767131402@qq.com",data);
    }
}
