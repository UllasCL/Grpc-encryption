package com.ullas.grpcEncryption.servers;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessageReqRes;
import com.ullas.grpcEncryption.GreeterGrpc;
import com.ullas.grpcEncryption.HelloRequest;
import com.ullas.grpcEncryption.HelloResponse;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
import com.ullas.grpcEncryption.utils.EncryptionUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 * The type Hello world client.
 */
public class HelloWorldClient {
  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());
  /**
   * The constant private_key.
   */
  public static String private_key
      = "";
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
  private GreeterGrpc.GreeterBlockingStub blockingStub;

  /**
   * Instantiates a new Hello world client.
   *
   * @param hostname the hostname
   * @param port     the port
   */
  public HelloWorldClient(String hostname, int port) {
    channel = ManagedChannelBuilder.forAddress(hostname, port)
        .usePlaintext()
        .build();
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    HelloWorldClient client = new HelloWorldClient("localhost", 42423);
    String name = args.length > 0 ? args[0] : "unknown";

    try {
      client.greetEncrypted(name);
    } finally {
      client.shutdown();
    }
  }

  /**
   * Generate random encryption key string.
   *
   * @param privateKeyForEncryption the private key for encryption
   * @return the string
   */
  private static String generateRandomEncryptionKey(String privateKeyForEncryption) {
    String key = UUID.randomUUID().toString().replace("-", "");
    try {
      responseEncryptionKey = EncryptionUtil.getEncryptedString(key,
          getPrivateKeyFromString(privateKeyForEncryption));
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
    return key;
  }

  /**
   * Load public key key.
   *
   * @param stored the stored
   * @return the key
   * @throws GeneralSecurityException the general security exception
   */
  private static PrivateKey getPrivateKeyFromString(String stored) throws GeneralSecurityException {
    byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    PKCS8EncodedKeySpec spec =
        new PKCS8EncodedKeySpec(data);
    KeyFactory fact = KeyFactory.getInstance("RSA");
    return fact.generatePrivate(spec);
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
  public void greetEncrypted(String name) {
    logger.info("Trying to greet " + name);
    try {
      String encryptionKey = generateRandomEncryptionKey(private_key);
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();
      EncryptedMessageReqRes encReq =
          EncryptedMessageReqRes.newBuilder()
              .setPayload(
                  ByteString.copyFrom(Objects
                      .requireNonNull(AesCryptUtil.encrypt(request.toByteArray(), encryptionKey))))
              .setEncRandomKey(responseEncryptionKey)
              .build();
      EncryptedMessageReqRes response = blockingStub.sayEncryptedHello(encReq);
      HelloResponse responseFromServer =
          HelloResponse.parseFrom(AesEncryptionUtil.decrypt(response.getPayload().toByteArray()));
      logger.info("Response: " + responseFromServer);
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Request to grpc server failed", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

