package com.ullas.grpcEncryption.utils;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessage;
import java.security.GeneralSecurityException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * The type Modify request util.
 */
@Service
public class ModifyRequestUtil {

  /**
   * The Random util.
   */
  @Autowired
  private RandomUtil randomUtil;
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
   * Modify req t.
   *
   * @param <ReqT>  the type parameter
   * @param message the message
   * @return the req t
   */
  public <ReqT> ReqT modifyRequest(final ReqT message) {
    String secretKey = randomUtil.getRandomDecryptionKey(
        ((EncryptedMessage) message).getKey().toByteArray());
    byte[] decBytes = null;
    try {
      decBytes =
          aesCryptUtil.decrypt(((EncryptedMessage) message).getData().toByteArray(),
              secretKey);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assert decBytes != null;
    return (ReqT) EncryptedMessage.newBuilder().setData(ByteString.copyFrom(decBytes))
        .setKey(ByteString.copyFromUtf8(secretKey))
        .build();
  }
}
