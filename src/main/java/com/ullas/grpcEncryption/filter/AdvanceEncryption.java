package com.ullas.grpcEncryption.filter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AdvanceEncryption {
  private static final String ALGORITHM = "AES";
  private byte[] key;

  public AdvanceEncryption(byte[] key) {
    this.key = key;
  }

  /**
   * Encrypts the given plain text
   *
   * @param plainText The plain text to encrypt
   */
  public byte[] encrypt(byte[] plainText) throws Exception {
    SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    return cipher.doFinal(plainText);
  }

  /**
   * Decrypts the given byte array
   *
   * @param cipherText The data to decrypt
   */
  public byte[] decrypt(byte[] cipherText) throws Exception {
    SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    return cipher.doFinal(cipherText);
  }
}

