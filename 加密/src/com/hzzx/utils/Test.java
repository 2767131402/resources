package com.hzzx.utils;


public class Test {
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		String text = "http://127.0.0.1:8080/tel/send?signName=郑磊&phoneNum=15313602580&templateCode=vhfj&hospitalId=10201&date=1537090234766";
		// 使用AES对信息加密
		String key = AESUtils.generateKey();
		String encryptText = AESUtils.encryptData(key, text);
		// 使用RSA对AES的密钥加密
		try {
			String doubleKey = RSAUtils.encryptByPublicKey(key);
			String doubleencryptText = RSAUtils.encryptByPublicKey(encryptText);
			System.out.println("要传到服务端的密文：" + doubleencryptText);
			System.out.println("要传到服务端的密钥：" + doubleKey);
			
			rse_aesTest(doubleencryptText, doubleKey);
			long endTime = System.currentTimeMillis();
			long timeLong = endTime - startTime;
			//long类型时间差转为double类型时间差，单位毫秒
			double timeDouble= Double.parseDouble(Long.toString(timeLong));
			System.out.println("该方法执行时间为" + timeDouble+ "毫秒，即" + timeDouble/(double)1000 + "秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void rse_aesTest(String doubleencryptText,String doubleKey) {
		try {
			// RSA解密
			String key = RSAUtils.decryptByPrivateKey(doubleKey);
			String encrtptText = RSAUtils.decryptByPrivateKey(doubleencryptText);
			// AES解密
			String text = AESUtils.decryptData(key, encrtptText);
			// 解密完成的明文
			System.err.println("明文：" + text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
