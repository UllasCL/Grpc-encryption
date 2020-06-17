package com.ullas.grpcEncryption.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.springframework.stereotype.Service;


/**
 * The type Encryption util.
 */
@Service
public final class EncryptionUtil {

  /**
   * Gets encrypted string.
   *
   * @param respose    the respose
   * @param privateKey the private key
   * @return the encrypted string
   */
  public byte[] getEncryptedString(byte[] respose, PrivateKey privateKey) {

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
      encrypted = cipher.doFinal(respose);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return encrypted;
  }

  /**
   * Gets decrypted string private key.
   *
   * @param text       the text
   * @param privateKey the private key
   * @return the decrypted string private key
   */
  public String getDecryptedStringPrivateKey(byte[] text, PrivateKey privateKey) {
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
    byte[] encrypted = text;
    String finalString = null;
    try {
      finalString = new String(cipher.doFinal(encrypted), UTF_8);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return finalString;
  }
}
