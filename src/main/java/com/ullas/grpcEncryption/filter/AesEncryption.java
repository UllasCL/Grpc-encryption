package com.ullas.grpcEncryption.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryption {

  private static final String ALGORITHM = "AES";
  private static final String myEncryptionKey = "ThisIsFoundation";
  private static final String UNICODE_FORMAT = "UTF8";

  public static String encrypt(String valueToEnc, Key key) throws Exception {
    Cipher c = Cipher.getInstance(ALGORITHM);
    c.init(Cipher.ENCRYPT_MODE, key);
    byte[] encValue = c.doFinal(valueToEnc.getBytes(UNICODE_FORMAT));
    return new String(encValue);
  }

  public static String decrypt(String encryptedValue, Key key) throws Exception {
    Cipher c = Cipher.getInstance(ALGORITHM);
    c.init(Cipher.DECRYPT_MODE, key);
    byte[] decValue = c.doFinal(encryptedValue.getBytes(UNICODE_FORMAT));
    return Arrays.toString(Base64.getEncoder().encode(decValue));
  }

  private static Key generateKey() throws Exception {
    byte[] keyAsBytes;
    keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
    Key key = new SecretKeySpec(keyAsBytes, ALGORITHM);
    return key;
  }

  public static void main(String[] args) throws Exception {
    //
    //    String value = "password1";
    //    Key key= generateKey();
    //    String valueEnc = AesEncryption.encrypt(value,key);
    //    String valueDec = AesEncryption.decrypt(valueEnc,key);
    //
    //    System.out.println("Plain Text : " + value);
    //    System.out.println("Encrypted : " + valueEnc);
    //    System.out.println("Decrypted : " + valueDec);

    String text = "Hello World";
    String key = "Bar12345Bar12345";
    // Create key and cipher
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");
    // encrypt the text
    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    byte[] encrypted = cipher.doFinal(text.getBytes());
    System.err.println(new String(encrypted, StandardCharsets.UTF_16));
    // decrypt the text
    cipher.init(Cipher.DECRYPT_MODE, aesKey);
    String decrypted =
        new String(cipher.doFinal(Arrays.toString(encrypted).getBytes()));
    System.err.println(decrypted);
  }

}