package com.ullas.grpcEncryption.utils;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The type Aes crypt util.
 */
@Service
public final class AesCryptUtil {

  /**
   * The constant LOGGER.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(AesCryptUtil.class);

  /**
   * Decrypt string.
   *
   * @param bytesToDecrypt the bytes to decrypt
   * @param secret         the secret
   * @return the string
   */
  public byte[] decrypt(byte[] bytesToDecrypt, String secret) {
    try {
      Key aesKey = new SecretKeySpec(secret.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      // decrypt the text
      cipher.init(Cipher.DECRYPT_MODE, aesKey);
      return cipher.doFinal(bytesToDecrypt);
    } catch (Exception e) {
      LOGGER.info("#### EXCEPTION WHILE DECRYPTING : {}" + e.getMessage());
    }
    return null;
  }


  /**
   * Encrypt byte [ ].
   *
   * @param bytesToEncrypt the bytes to encrypt
   * @param secret         the secret
   * @return the byte [ ]
   */
  public byte[] encrypt(byte[] bytesToEncrypt, String secret) {
    try {
      Key aesKey = new SecretKeySpec(secret.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      // encrypt the text
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      return cipher.doFinal(bytesToEncrypt);
    } catch (Exception e) {
      LOGGER.info("Error while encrypting: " + e.toString());
    }
    return null;
  }
}
