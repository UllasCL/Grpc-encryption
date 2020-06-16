package com.ullas.grpcEncryption;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
import com.ullas.grpcEncryption.utils.EncryptionUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The type Hello world client.
 */
public class TestClient {

  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(TestClient.class.getName());
  /**
   * The constant private_key.
   */
  public static String public_key
      =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3GkdjRkPT6A1M3xSlu+F"
      + "1P7M0XmUV4R4fczGjoYUYM84vlYN2iZwIOBaKIQUwrb620bRxGxcObslSww5FGNy"
      + "e/KjEE7qGAod1d95edmFv7C48hG7v2oZAIGSivg5yLeemZ/OIBYwpOjzgGWCH3tY"
      + "qJl5CiYdwv8DJTSfVdWe/7IELEu0u4kehpcRdQ55U2OR574+20C5RN0esS9Vb7oX"
      + "YXglFsfFl8Vdr2ZJNyjHQvIDTTZMOY/u53B6NgnOEey3xWLsMfdV0aXpU7A89D5B"
      + "MbLuBT5B5RM8Wy1zhEeGtz5lNg2uAem37ddij+E+inhqyV92ZaQaa4tTqcY4tgda"
      + "CwIDAQAB";
  /**
   * The constant responseEncryptionKey.
   */
  private static String responseEncryptionKey;
  /**
   * The Channel.
   */
  private final ManagedChannel channel;
  /**
   * The Blocking stub.
   */
  private final TestServiceGrpc.TestServiceBlockingStub blockingStub;

  /**
   * Instantiates a new Hello world client.
   *
   * @param hostname the hostname
   * @param port     the port
   */
  public TestClient(String hostname, int port) {
    channel = ManagedChannelBuilder.forAddress(hostname, port)
        .usePlaintext()
        .build();
    blockingStub = TestServiceGrpc.newBlockingStub(channel);
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    TestClient client = new TestClient("localhost", 6565);
    String name = args.length > 0 ? args[0] : "unknown";

    try {
      client.getConfig(name);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Generate random encryption key string.
   *
   * @param publicKeyForEncryption the public key for encryption
   * @return the string
   */
  private static String generateRandomEncryptionKey(String publicKeyForEncryption) {
    String key ="1234567890123456";// UUID.randomUUID().toString().replace("-", "");
    try {
      responseEncryptionKey = EncryptionUtil.getEncryptedStringPublicKey(key,
          getPublicKeyFromString(publicKeyForEncryption));
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
    return key;
  }

  /**
   * Gets public key from string.
   *
   * @param stored the stored
   * @return the public key from string
   * @throws GeneralSecurityException the general security exception
   */
  private static PublicKey getPublicKeyFromString(String stored) throws GeneralSecurityException {
    byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    KeyFactory fact = KeyFactory.getInstance("RSA");
    return fact.generatePublic(spec);
  }

  /**
   * Shutdown.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Greet encrypted.
   *
   * @param name the name
   */
  public void getConfig(String name) {
    logger.info("Trying to greet " + name);
    try {
      String encryptionKey = generateRandomEncryptionKey(public_key);
      Request1 request = Request1.newBuilder().setData(name).build();
      EncryptedMessage encReq =
          EncryptedMessage.newBuilder()
              .setData(
                  ByteString.copyFrom(Objects
                      .requireNonNull(AesCryptUtil.encrypt(request.toByteArray(), encryptionKey))))
              .setKey(responseEncryptionKey)
              .build();
      EncryptedMessage response = blockingStub.getConfig(encReq);
      Response1 responseFromServer =
          Response1.parseFrom(AesEncryptionUtil.decrypt(response.getData().toByteArray()));
      logger.info("Response: " + responseFromServer);
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Request to grpc server failed", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

