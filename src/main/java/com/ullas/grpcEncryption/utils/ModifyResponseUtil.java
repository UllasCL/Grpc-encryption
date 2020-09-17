package com.ullas.grpcEncryption.utils;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessage;
import java.security.GeneralSecurityException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The type Modify response util.
 */
@Service
public class ModifyResponseUtil {

  /**
   * The Random util.
   */
  @Autowired
  private RandomUtil randomUtil;
  /**
   * The Encryption util.
   */
  @Autowired
  private EncryptionUtil encryptionUtil;
  /**
   * The Aes crypt util.
   */
  @Autowired
  private AesCryptUtil aesCryptUtil;
  /**
   * The Response encryption key.
   */
  private static byte[] responseEncryptionKey;

  /**
   * The Private key.
   */
  @Value("${aes.private_key}")
  public String privateKey;

  /**
   * Modify response res t.
   *
   * @param <ResT>  the type parameter
   * @param message the message
   * @return the res t
   */
  public <ResT> ResT modifyResponse(final ResT message) {
    String encryptionKey = generateRandomEncryptionKey();
    byte[] encBytes = null;
    try {
      encBytes =
          aesCryptUtil.encrypt(((EncryptedMessage) message).getData().toByteArray(),
              encryptionKey);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assert encBytes != null;
    return (ResT) EncryptedMessage.newBuilder().setData(ByteString.copyFrom(encBytes))
        .setKey(ByteString.copyFrom(responseEncryptionKey))
        .build();
  }

  /**
   * Generate random encryption key string.
   *
   * @return the string
   */
  private String generateRandomEncryptionKey() {
    String key = UUID.randomUUID().toString().replace("-", "");
    try {
      responseEncryptionKey =
          encryptionUtil.getEncryptedString(key.getBytes(),
              randomUtil.getPrivateKeyFromString(privateKey));
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
    return key;
  }
}
