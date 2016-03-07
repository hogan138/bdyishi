package com.fding.activity.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.Security;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	/**
	 * 密钥算法 java6支持56位密钥，bouncycastle支持64位
	 */
	public static final String KEY_ALGORITHM = "AES";

	/**
	 * 加密/解密算法/工作模式/填充方式
	 * 
	 * JAVA6 支持PKCS5PADDING填充方式 Bouncy castle支持PKCS7Padding填充方式
	 */
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

	/**
	 * 
	 * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
	 * 
	 * @return byte[] 二进制密钥
	 */
	// public static byte[] initkey() throws Exception {

	// //实例化密钥生成器
	// Security.addProvider(new
	// org.bouncycastle.jce.provider.BouncyCastleProvider());
	// KeyGenerator kg=KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
	// //初始化密钥生成器，AES要求密钥长度为128位、192位、256位
	//// kg.init(256);
	// kg.init(128);
	// //生成密钥
	// SecretKey secretKey=kg.generateKey();
	// //获取二进制密钥编码形式
	// return secretKey.getEncoded();
	// 为了便于测试，这里我把key写死了，如果大家需要自动生成，可用上面注释掉的代码
	// return new byte[] { 0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c, 0x01,
	// 0x03, 0x09, 0x07,
	// 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f, 0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01,
	// 0x0a, 0x0a,
	// 0x01, 0x09, 0x06, 0x07, 0x09, 0x0d };
	// }

	/**
	 * 转换密钥
	 * 
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 */
	public static Key toKey(byte[] key) throws Exception {
		// 实例化DES密钥
		// 生成密钥
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
		return secretKey;
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密后的数据
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		/**
		 * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
		 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
		 */
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		// 初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密后的数据
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 欢迎密钥
		Key k = toKey(key);
		/**
		 * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现
		 * Cipher.getInstance(CIPHER_ALGORITHM,"BC")
		 */
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);
		// 执行操作
		return cipher.doFinal(data);
	}

	public static String getKeyToFront(byte[] key) {
		StringBuffer mkey = new StringBuffer();
		for (int i = 0; i < key.length; i++) {

			mkey.append(Integer.toHexString(key[i] & 0xFF));
		}
		return mkey.toString();

	}

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		String str = "1";
		System.out.println("原文：" + str);

		// 初始化密钥
		byte[] key;
		try {
			// key = AES256Encryption.initkey();

			// String rdm=AES256Encryption.createRandomString(32);
			String rdm = "dddddddddddddddddddddddddddddddd";
			System.out.println("rdm:" + rdm);
			key = rdm.getBytes();

			System.out.print("密钥：");
			StringBuffer mkey = new StringBuffer();
			for (int i = 0; i < key.length; i++) {

				mkey.append(Integer.toHexString(key[i] & 0xFF));
			}

			// mkey为正确的密钥，需要传给前端获取
			System.out.println(mkey);

			// 加密数据
			byte[] data = AESUtil.encrypt(str.getBytes(), key);
			System.out.print("加密后：");
			for (int i = 0; i < data.length; i++) {
				System.out.printf("%x", data[i]);
			}
			System.out.print("\n");

			// 解密数据
			data = AESUtil.decrypt(data, key);
			System.out.println("解密后：" + new String(data));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', '0', '1' };// 最后又重复两个0和1，因为需要凑足数组长度为64

	private static Random random = new Random();

	// 生成指定长度的随机字符串
	public static synchronized String createRandomString(int length) {
		if (length > 0) {
			int index = 0;
			char[] temp = new char[length];
			int num = random.nextInt();
			for (int i = 0; i < length % 5; i++) {
				temp[index++] = ch[num & 63];// 取后面六位，记得对应的二进制是以补码形式存在的。
				num >>= 6;// 63的二进制为:111111
				// 为什么要右移6位？因为数组里面一共有64个有效字符。为什么要除5取余？因为一个int型要用4个字节表示，也就是32位。
			}
			for (int i = 0; i < length / 5; i++) {
				num = random.nextInt();
				for (int j = 0; j < 5; j++) {
					temp[index++] = ch[num & 63];
					num >>= 6;
				}
			}
			return new String(temp, 0, length);
		} else if (length == 0) {
			return "";
		} else {
			throw new IllegalArgumentException();
		}
	}

}
