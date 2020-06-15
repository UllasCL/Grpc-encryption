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
      = "MIIEpQIBAAKCAQEA3ukZZjDSae5K+snb/ZZKhlTgvo7HPos3ElNtlLt1srplR2+hNBlAa25EMCgDfucLESWT1CzU2VGcWDP69YxF0upz20rlBlHrzJ/VaNKU0i6xGpn+jojHssjgHur4ykS2bCIXY5xByFn3j7cEEljkktVDypyCSVGAEhWPeiP4Ft9uXW2PZHR8S5kzbs2FFtdmA9j0UEGCIjePVrzx3yDMx88gJpbX2zPvhwoFIvRVP48Iq+6wBLaUzpOgPNVg/YtnJE13u2Q27bA0rcaGxn7+2GytHPysaJbneMU6oqkf2/LqEkAGwqyAUM15/i+K3XI8bJ47oLhrZtstd6F0aulYEwIDAQABAoIBAQCIGI9+Wf6N2ISE2+vpxvHXVEknbyrs37iPAsrbzRxONAmT9O7aTTSMovUU5clEmLGkFWpNV9XYnUA0hgAKHUugO67iy7ZLBodOFqQIbNGaBhHxaOYHhjFO0eAsYE/nxs4N9T6MfaGYPn2sjBP7U7SyhWzvbmEJfK4R3IIOE5nr8/h9YYWpUfAB8fheixd3ZhzNW9tCpBAGiTW27I8LjAn7vjlWF2g7sM8bien7zlGZv0OAmN3BKuWgBxkrvRF1v+LqSc4SBmN1EKSZRdJYrkGcwEv0nVpieAduIE7TBteZzk+AFUGxzD+wngxPj2BobTctLebxnNjvjTPThJtJQe1ZAoGBAPo9l6DyBphPiAcsWaeEQp+C+VjJxCEqXn9dyzrlxDcp4Vd+lkJle0V/Maw3EPIp9M7Byxzzkh9omOxYZwHFQnrGK88VEXPsk59vLn+e25/jjlcHDHGP8VxQkoSgXhteCgFzYr2FCkIXjABD5S33fd89vBtZhfA0itbCYrOFgFW1AoGBAOQKerWo+V6Z/lXcqkX7x0kjuNAeleDo8hVDPdMZQTeWi1hLQNYIj0vompqkhh2VVnogAdEZ2irRepvRZvY+c0Kv9M6cEiVjf4xVLqZ1uI8YLD0+Pkp4nHjTc/HJ+w1kwpiROBzGXTTVFc9l+w3xWgXpuKXHvTkY2WYg9tKroROnAoGBAO4k6YU4b0BKB9lTwqqiOskU2vk9YtJxQOcTOtJ9UUNyLsqWZKchbrQPvRT4fr8HYLvXP9X4kuojH81kv/q2YHL43z4+/ZIF5eNH6sd3RemepJp70cLNStQgtZ9UAtsmnn0M8z4LO1z0jb6+3n7lVju6w6Z/hvC3AB4rdcUE2sI1AoGBAJ4LuUIf6zyqg8AkYIqCVnsVW+g52mx3Zhhgcv/UWAPbvZWjCjuM0Sut+UDGWAE22Clp93hhb3gPmOdXOQHfBGkqpANx3Y22KNcszlkl65SZD0ge1HMupluDSa9A6lM9CzVMKFShEkYkjKoiJt5h8J96fbO//CAPXdTPceFOfCOvAoGAInlvBw2H5s6ZkJYBoqjpBKsIC3Hb4XRS8KIlOtLVOiKAtPTdRlWNig4XlpdhyXIIiMnZZAhaG9y4Hdiy7y+JU/cZ9VGrjdXXsXiBbwfCN9qHoct3jsvNBXTEwL1Us19Cq+z8377RMOR1pXuKlcypdcPZ0P59HElV4oY94O73zBI=";
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

