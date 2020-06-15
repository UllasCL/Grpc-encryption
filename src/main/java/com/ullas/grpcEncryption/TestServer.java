package com.ullas.grpcEncryption;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.TestServiceGrpc.TestServiceImplBase;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
import com.ullas.grpcEncryption.utils.EncryptionUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * The type Hello world server.
 */
public class TestServer {

  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(
      com.ullas.grpcEncryption.HelloWorldServer.class.getName());

  /**
   * The constant public_key.
   */
  private static String private_key
      = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDMfPQiepv4Ksbx\n"
      + "z5ZQXsFrFoPfwpQDJ2BO5q5jvirEFuuAHiZS37NmBuhe/gX2lnRDPhkSa5H6fO3G\n"
      + "Ssq4a1JTDwNpyi2e58JONeWac6HYCBZpDSShIAzPDn7rv9RfEITLOabBHI7/ojis\n"
      + "wJeTXmL/qOR5RaZptLxWdTCvffVppW/Fo9d5Q0egNTK5QeF5g/CUa+S4dH1/woA8\n"
      + "X/XrxNFmQNGhQaFdtKaTJrz9JArgJQbXuZBHDZxhwXnugfQXKDK6Kdnb16bCHN8d\n"
      + "I5K91PH5fM67Y232rxtBo5HWqCakZLdK276kfu7MsqLlkOB/iKt+QI9KVFtQW+0y\n"
      + "XRhqhrW/AgMBAAECggEBAJaDCncuZArgAZsOvobdl+QoqFxUrfk8TGkG5ilIWdYH\n"
      + "QMQGbRLm0Pcx8muo8d60k1LDLFUglEwZFDK17kybwfacwFhnlfi2gZBrn1p17hRC\n"
      + "r6wXHnTyUDs/YjBH/BsU34luH81YJfEHCEwvXeH8wUWInNKeb4SyoUXJ8FHbln1x\n"
      + "E+DyEMGrUIkzdB2cfdr0Tw3bmYAFEbrWvrhPXak0eXqEvXrWubWeioCmLHwugpSa\n"
      + "CpBxScd+mZjPfUo8i7rJD9YPnfwJrsgtXVMI1tQRXLHj68wTK2sbDQ2VXrHSsY1E\n"
      + "U88SFs7BahtSpiXhcNPZyDfnSsiB6ZcvqodVkmWyRIECgYEA92v5vF76ACK9dRPK\n"
      + "pT5toAGQrkFUV1VnP60b6sXt9fxPQWIq/YVnX3HueONg5uHr1VsJDZ1cbMZp1YKz\n"
      + "ibx7scIvq9PHZcdDz2PYPYlhtw+pChs6Tm8GzLGoqvs9lfGYQwD9YPQ+FohSXRD0\n"
      + "ZMucz2dOFL3n+ugK26nCQqWndiECgYEA05PqJFaa/uMpMJ9hdUYKPGU+itad+B9L\n"
      + "Sn/X2BzLn7GpWHYW3ClDsVaP9runJbedkx9/v5KaxiCrjmtviWdf6NpYhGOfOHfZ\n"
      + "QU97Z4/2ngVIT9bhqVoL4w9gzkIOqV2ca4ZIxbkKYSx1aHuHoiv+pW3TYDzFm7k4\n"
      + "h8TJMB6e798CgYBJQYLzrvs+RwrpOy7otg8nLM91iIo8QWEMjWiSnn/SMR4WMcag\n"
      + "wXrV25nL2Sa7tXBp/0Yy7cCLxENZoBUgVtoYy3Hp4g5LqC//m4RO0/pGhTB7eYxN\n"
      + "5LmSqZdS6IhXeOyoCgb7SdWIPlyEiZySq2aGUgPgSEoDE+kDGMQXj4yBgQKBgQC+\n"
      + "yX30oKgJuhvVfZMrZioqxf08w0UVjlqdZmTjuyue6FzVqT757rfc/Tq6egLCjy2n\n"
      + "klrnDDNHAATTW2vrkm05E1OIpjDYgbJGI38bJwVy/z5yyEkbJljbum1H8Oc5sHEx\n"
      + "wJh0u2Rxtd51hhcLlPJ+iGd4O7TRtJcWTsxA+QvHLwKBgE7+A/VnRWTeVNf+5dEh\n"
      + "zWF1lj4wr+1oM80bCCwqOSBfD7BwDv7+1R+BCCicjmxfJkSlfx3xIKpwkRVB5X2M\n"
      + "oE4Yj8XecUMBfkT88BFKW2P9TOxKjWNPF1/Lkf+KkTrzHGiDdTztj7k7TwNN6aDc\n"
      + "ZeGY+/cpLH0BStV6YCl6ZkwJ";
  /**
   * The Port.
   */
  private int port = 42423;
  /**
   * The Server.
   */
  private Server server;

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    logger.info("Server startup. Args = " + Arrays.toString(args));
    final TestServer
        testServer = new TestServer();

    testServer.start();
    testServer.blockUntilShutdown();
  }

  /**
   * Generate random encryption key string.
   *
   * @param privateKeyForDecryption the private key for decryption
   * @param encRendomKey            the enc rendom key
   * @return the string
   */
  private static String getRandomDecryptionKey(String privateKeyForDecryption,
                                               String encRendomKey) {
    try {
      return EncryptionUtil.getDecryptedStringPrivateKey(encRendomKey,
          getPrivateKeyFromString(privateKeyForDecryption));
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
    return null;
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
   * Gets private key from string.
   *
   * @param stored the stored
   * @return the private key from string
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
   * Start.
   *
   * @throws Exception the exception
   */
  private void start() throws Exception {
    logger.info("Starting the grpc server");

    server = ServerBuilder.forPort(port)
        .addService(new TestServiceImpl())
        .build()
        .start();

    logger.info("Server started. Listening on port " + port);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** JVM is shutting down. Turning off grpc server as well ***");
      TestServer.this.stop();
      System.err.println("*** shutdown complete ***");
    }));
  }

  /**
   * Stop.
   */
  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Block until shutdown.
   *
   * @throws InterruptedException the interrupted exception
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * The type Greeter.
   */
  private class TestServiceImpl extends TestServiceImplBase {

    /**
     * Gets config.
     *
     * @param request          the request
     * @param responseObserver the response observer
     */
    @Override
    public void getConfig(final EncryptedMessage request,
                          final StreamObserver<EncryptedMessage> responseObserver) {

      String secretkey = getRandomDecryptionKey(private_key, request.getKey());
      Response1 response;
      Request1 reqFromClient = null;
      try {
        reqFromClient =
            Request1.parseFrom(AesCryptUtil.decrypt(request.getData().toByteArray(),
                secretkey));
      } catch (Exception e) {
        e.printStackTrace();
      }
      EncryptedMessage encRes =
          EncryptedMessage.newBuilder().build();
      try {
        response = Response1.newBuilder().setData(
            "hello " + Objects.requireNonNull(reqFromClient).getData()
        ).build();
        encRes = EncryptedMessage.newBuilder().setData(
            ByteString.copyFrom(AesEncryptionUtil.encrypt(response.toByteArray())))
            .build();
      } catch (Exception e) {
        e.printStackTrace();
      }

      responseObserver.onNext(encRes);
      responseObserver.onCompleted();
    }

    /**
     * Gets user.
     *
     * @param request          the request
     * @param responseObserver the response observer
     */
    @Override
    public void getUser(final EncryptedMessage request,
                        final StreamObserver<EncryptedMessage> responseObserver) {
      super.getUser(request, responseObserver);
    }
  }
}