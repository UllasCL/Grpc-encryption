package com.ullas.grpcEncryption.utils;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * The type Aes encryption util.
 */
public class AesEncryptionUtil {
  /**
   * The constant key.
   */
  private static final String key = "Bar12345Bar12345";

  /**
   * Encrypt string.
   *
   * @param data the data
   * @return the string
   * @throws Exception the exception
   */
  public static byte[] encrypt(byte[] data) throws Exception {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");
    // encrypt the text
    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    return cipher.doFinal(data);
  }

  /**
   * Decrypt byte [ ].
   *
   * @param data the data
   * @return the byte [ ]
   * @throws Exception the exception
   */
  public static byte[] decrypt(byte[] data) throws Exception {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");
    // decrypt the text
    cipher.init(Cipher.DECRYPT_MODE, aesKey);
    return cipher.doFinal(data);
  }
}
