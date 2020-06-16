package com.ullas.grpcEncryption;

import io.grpc.stub.StreamObserver;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.TestServiceGrpc.TestServiceImplBase;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
import com.ullas.grpcEncryption.utils.EncryptionUtil;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Logger;
import org.lognet.springboot.grpc.GRpcService;

/**
 * The type Greeter.
 */
@GRpcService
public class TestServer extends TestServiceImplBase {

  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(TestServer.class.getName());

  /**
   * The constant public_key.
   */
  private static String private_key
      = "";

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
}