package com.ullas.grpcEncryption;

import com.google.protobuf.ByteString;
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
@Service
public class HelloWorldClient {
  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());
  /**
   * The constant private_key.
   */
  public static String private_key
      = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkeqfp3JF"
      + "/oZKT9cHBPWVIUCAqrXYvbCZmDGCQsgWJ0mO9rViDEjn"
      + "+4Yqi5GkEcfkLsbFG66hgtlo9He1xiCPF0GsV97YAW8lL+qSEhG9DlNTEqK2LlRyhPqsF8DDQ+dy"
      + "+7rk3O5A1ZzJg0GlMtqx1Se9p/W3ZqulNSDQkJ1AB+5Fpxmff/88eO8v4gbQII7nVEv28FdQ4S1LsA"
      + "+8iGiTvTyGQ6t7mUgLlZy"
      + "/C1W8LJz99CrtU8roZpTvCgtLEdv5AaYZFDNZ3s82ycDeAIBRDvBpfOe9lKeCdOLhUR1R5uRajJWN/CF"
      + "/7XIh5sWkvX+NqYHL1+QP30e4WVNMFzba/AgMBAAECggEARvO+YYzLmyIo5"
      + "/jY1zJT8ajW53IijHx76AOo2GJjLzv52MXha9E+AWxr8K7Vepxr0Zaaobxgyy9+p"
      + "+S0HAidXeT1fOl5spflswfxVlUG5kFas2cMTi1Jx2kQP6SIem1"
      + "+w0KZ1XC1WcQloEvnULdwl7wIMlGnxIEM2VCvZXUr915MEFg4FPOvGVR/+QPcf6Zi+XA"
      + "/kTlHYwp3e8M9WrYhuJnaFIs6OsXoDmtg2MI3"
      + "+Bv9Ddr9Gx3tdZumpsvV2qzbADMIE2PU4QaGygwVDsafZG1IrzAYnMPcZftSDjDy"
      + "+DEwTAu7gm1tfhXTiyWKSdByO8C0xTmrzxLG4KWvnZ3BoQKBgQDT29Y3RbO2oxnyp6oAL7w8jMadVEhXb"
      + "+9AR5OK1SA4YT43DlCFjfyF0R33Ex5Y5VQXFVikyGbNiQH0+Hszie2yDQ"
      + "/mb74IvLN2gtr7SJOUrLGOHse6PZFyxVDPp8mzsVdH7m3ZvSkt/COwsdcieqwsjcIaVmQdhFfJ"
      + "/YrpvMxeUQKBgQDGv60M9biICDUBybaOR5gjLPDGIvUP76IqHIOBnOSokiBuNvZAn9LZvQIG4/jkqsYd6Va6"
      + "+QAmz/W9cdYss+WeVwcAh7JII+JcRQuDdKWykgcNMZj5jfOIOP+bJb"
      + "/EEdPV2S6ZoTEw5YnfUJqPTF7GpKq6npmEnu/VA4fxD5UwDwKBgQCSy4Z4seE"
      + "+MXEvQ6F1k53YTqroJJgjjbm3TmEbF2XF6wtS1qZQg/tvNWCP7D1g1zYW8IGFXB5vV"
      + "/l1Jh8nrPB4KLjVsKN6JlYrTawx5S3tHGPC1+0ITNTVM4c0+0u1KLITc0/GSV2ZMVYBfphUma9nB6pw2V9xXFv"
      + "/56AzKNBqoQKBgQDAG3f2xfCnNF+gKcyWfGd24LKeoh2EWNnDoKoQd4mh0xUb0251isaxBnvWEzd6OAOl1"
      + "+yob5SQ74SVqFof/YdeZLp07bUK+/yJuYdmejLLh1DflOhLl8UlEAspePSCXcUHuJTNlLxKiiSZQX0UM"
      + "/qMQZBxMGotgb9Kg8472RcYSwKBgGmOv4Xj7XoP7eTQu0AjJgPv/CGxuKFYxX1/ndA62zwTjXlVleRRCgVjgICA"
      + "/1YScc/1+cfESGNzV+o0QM+a1drT7yTBQ6lDTCK3kuA9Sq82cnU8UkOQNfSuOA6TCpmeUDePOUiMtzJ/U0"
      + "+BZUeKMsitanY9qZ9NvOufSOYEns6F";
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

