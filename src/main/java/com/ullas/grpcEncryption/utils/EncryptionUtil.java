package com.ullas.grpcEncryption.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The type Encryption util.
 */
public final class EncryptionUtil {

  /**
   * The constant LOGGER.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);
  /**
   * The constant PUBLIC_KEY.
   */
  public static String public_key = "";

  /**
   * Gets encrypted string.
   *
   * @param request    the request
   * @param privateKey the private key
   * @return the encrypted string
   */
  public static String getEncryptedString(String request, PrivateKey privateKey) {

    Cipher cipher = null; //or try with "RSA"
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }

    try {
      if (cipher != null) {
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }

    byte[] encrypted = null;
    try {
      encrypted = cipher.doFinal(request.getBytes(UTF_8));
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return Base64.getEncoder().encodeToString(encrypted);
  }

  /**
   * Gets decrypted string.
   *
   * @param text      the text
   * @param publicKey the public key
   * @return the decrypted string
   */
  public static String getDecryptedString(String text, PublicKey publicKey) {
    Cipher cipher = null; //or try with "RSA"
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }
    try {
      if (cipher != null) {
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    byte[] encrypted = Base64.getDecoder().decode(text);
    String finalString = null;
    try {
      finalString = new String(cipher.doFinal(encrypted), UTF_8);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return finalString;
  }

  /**
   * Gets private key.
   *
   * @param privateKey the private key
   * @return the private key
   * @throws InvalidKeySpecException the invalid key spec exception
   */
  public static PrivateKey getPrivateKey(String privateKey) throws InvalidKeySpecException {
    byte[] byteKey = Base64.getDecoder().decode(privateKey.getBytes());
    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(byteKey);
    KeyFactory kf = null;
    try {
      kf = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      LOGGER.info("#### EXCEPTION" + e.getMessage());
    }
    return kf.generatePrivate(privateKeySpec);
  }

  /**
   * Gets decrypted string private key.
   *
   * @param text       the text
   * @param privateKey the private key
   * @return the decrypted string private key
   */
  public static String getDecryptedStringPrivateKey(String text, PrivateKey privateKey) {
    Cipher cipher = null; //or try with "RSA"
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }
    try {
      if (cipher != null) {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    byte[] encrypted = Base64.getDecoder().decode(text);
    String finalString = null;
    try {
      finalString = new String(cipher.doFinal(encrypted), UTF_8);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return finalString;
  }

  /**
   * Gets encrypted string public key.
   *
   * @param request   the request
   * @param publicKey the public key
   * @return the encrypted string public key
   */
  public static byte[] getEncryptedStringPublicKey(String request, PublicKey publicKey) {

    Cipher cipher = null; //or try with "RSA"
    try {
      cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }

    try {
      if (cipher != null) {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }

    byte[] encrypted = null;
    try {
      encrypted = cipher.doFinal(request.getBytes(UTF_8));
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return encrypted;
  }
}
