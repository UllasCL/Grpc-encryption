package com.ullas.grpcEncryption.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Aes crypt util.
 */
public final class AesCryptUtil {

  /**
   * The constant LOGGER.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(AesCryptUtil.class);

  /**
   * The constant secretKey.
   */
  private static SecretKeySpec secretKey;

  /**
   * The Key.
   */
  private static byte[] key;


  /**
   * Sets key.
   *
   * @param myKey the my key
   */
  public static void setKey(String myKey) {
    MessageDigest sha = null;
    try {
      key = myKey.getBytes("UTF-8");
      sha = MessageDigest.getInstance("SHA-1");
      key = sha.digest(key);
      key = Arrays.copyOf(key, 16);
      secretKey = new SecretKeySpec(key, "AES");
    } catch (NoSuchAlgorithmException e) {
      LOGGER.info(e.getMessage());
    } catch (UnsupportedEncodingException e) {
      LOGGER.info(e.getMessage());
    }
  }

  /**
   * Decrypt string.
   *
   * @param strToDecrypt the str to decrypt
   * @param secret       the secret
   * @return the string
   */
  public static String decrypt(String strToDecrypt, String secret) {
    try {
      setKey(secret);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    } catch (Exception e) {
      LOGGER.info("#### EXCEPTION WHILE DECRYPTING : {}" + e.getMessage());
    }
    return null;
  }


  public static String encrypt(String strToEncrypt, String secret) {
    try {
      setKey(secret);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return Base64.getEncoder()
          .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      LOGGER.info("Error while encrypting: " + e.toString());
    }
    return null;
  }
}
