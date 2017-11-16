package com.newabelhce;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;

public class MainDES {
	public static byte[] password = { 0x73, 0x42, (byte) 0x95, 0x12, 0x34, (byte) 0x86, 0x59, 0x78 };
	public static byte[] tmpkey = new byte[8];

	public MainDES() {
	}
	
	public static void initkey(byte[] random) {
		byte[] res = encrypt(random, password);
		if(res == null) return;
		for(int i = 0; i < 8; i++) {
			tmpkey[i] = res[i];
		}
	}	
	
	public static byte[] encrypt(byte[] datasource) {
		try {
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, getKey(tmpkey));
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
    private static byte toByte(char c) {  
        byte b = (byte) "0123456789ABCDEF".indexOf(c);  
        return b;  
    } 
	
    /** 
     * 把16进制字符串转换成字节数组 
     *  
     * @param hex 
     * @return 
     */  
    public static byte[] hexStringToByte(String hex) {  
        int len = (hex.length() / 2);  
        byte[] result = new byte[len];  
        char[] achar = hex.toCharArray();  
        for (int i = 0; i < len; i++) {  
            int pos = i * 2;  
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));  
        }  
        return result;  
    } 	
	
	public static byte[] get4ByteFromStr(String str) {
		String tmp = str.substring(str.length() - 8, str.length());
		System.out.println(tmp);
		return ConvertUtil.str2Bcd(tmp);
		//return tmp.getBytes();
	}
    
	public static byte[] imei_id(String imei, String andoridid) {
		//String idstr = "00" + imei.substring(8, 14);
		String idstr = imei.substring(2,4) + imei.substring(8, 14);
		
		byte[] idbyte = ConvertUtil.str2Bcd(idstr);
		
		byte[] tmpid = new byte[8];
		Arrays.fill(tmpid, (byte) 0x00);
		tmpid[0] = idbyte[0];
		tmpid[1] = idbyte[1];
		tmpid[2] = idbyte[2];
		tmpid[3] = idbyte[3];
		
		//获取andoridid做密钥
		byte[] tmppwd = MainDES.hexStringToByte(andoridid.substring(0, 16));
		
		byte[] res = MainDES.encrypt(tmpid, tmppwd);
		
		idbyte[0] = res[0];
		idbyte[1] = res[1];
		idbyte[2] = res[2];
		idbyte[3] = res[3];
		
		
		return idbyte;
	}
	
	public static byte[] idmac(byte[] id) {
		byte[] tmp = new byte[8];
		for(int i = 0; i < 4; i++) {
			tmp[i] = id[i];
			tmp[i+4] = (byte) ~id[i];		
		}
		return mac(tmp);
	}
	
	public static byte[] mac(byte[] datasource) {
		byte[] res = encrypt(datasource);
		if(res == null) return null;
		byte[] ret = new byte[4];
		for(int i = 0; i < 4; i++) {
			ret[i] = (byte) (res[i] ^ res[i + 4]);
		}
		return ret;
	}
	
	public static byte[] decrypt(byte[] src) {
		try {
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, getKey(tmpkey));
			// 真正开始解密操作
			return cipher.doFinal(src);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 *
	 * @param arrBTmp
	 *            构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws Exception
	 */
	private static Key getKey(byte[] arrBTmp) throws Exception {
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(arrBTmp, "DES");

		return key;
	}
	

	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] datasource, byte[] password) {
		try {
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, getKey(password));
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] password) {
		try {
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, getKey(password));
			// 真正开始解密操作
			return cipher.doFinal(src);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
