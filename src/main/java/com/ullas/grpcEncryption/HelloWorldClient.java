package com.ullas.grpcEncryption;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.security.Key;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * The type Hello world client.
 */
public class HelloWorldClient {
  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());
  /**
   * The constant key.
   */
  private static final String key = "Bar12345Bar12345";
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

      client.greetEncrypted(name);
    } finally {
      client.shutdown();
    }
  }


  /**
   * Encrypt string.
   *
   * @param <T>  the type parameter
   * @param data the data
   * @return the string
   * @throws Exception the exception
   */
  public static byte[] encrypt(byte[] data) throws Exception {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");
    // encrypt the text
    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    byte[] encrypted = cipher.doFinal(data);
    return encrypted;
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
   * Greet.
   *
   * @param name the name
   */
  public void greet(String name) {
    logger.info("Trying to greet " + name);
    try {
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();

      HelloResponse response = blockingStub.sayHello(request);
      logger.info("Response: " + response.getMessage());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Request to grpc server failed", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Greet encrypted.
   *
   * @param name the name
   */
  public void greetEncrypted(String name) {
    logger.info("Trying to greet " + name);
    try {
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();
      //      encryptCheck(request);
      request.toByteArray();
      byte[] encReq1 = new byte[request.toByteArray().length];

      for (int i = 0; i < request.toByteArray().length; i++) {
        encReq1[i] = request.toByteArray()[i];
      }
      EncryptedMessageReqRes encReq =
          EncryptedMessageReqRes.newBuilder()
              .setPayload(ByteString.copyFrom(encrypt(request.toByteArray())))
              .build();
      EncryptedMessageReqRes response = blockingStub.sayEncryptedHello(encReq);
      logger.info("Response: " + response.getPayload());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Request to grpc server failed", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

