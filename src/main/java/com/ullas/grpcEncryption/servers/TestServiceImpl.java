package com.ullas.grpcEncryption.servers;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessage;
import com.ullas.grpcEncryption.Request1;
import com.ullas.grpcEncryption.Response1;
import com.ullas.grpcEncryption.TestServiceGrpc;
import com.ullas.grpcEncryption.interceptors.GrpcServerJwtInterceptor;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
import com.ullas.grpcEncryption.utils.EncryptionUtil;
import io.grpc.stub.StreamObserver;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import org.lognet.springboot.grpc.GRpcService;

/**
 * The type Test service.
 */
@GRpcService(interceptors = GrpcServerJwtInterceptor.class)
class TestServiceImpl extends TestServiceGrpc.TestServiceImplBase {
  /**
   * The constant private_key.
   */
  private static String private_key
      = "MIICXQIBAAKBgQCsURnxnPIi6oWS7jEJHVujP18uo/zVSbDz7yBpHWDzRvnmaDnb"
      + "TunFbrfJwnGI/9YRXLVEKPihv0pj/oeO06ARQaqMChY9BZTHETb0DGkOCzS5+7QW"
      + "QZkhsk3LvJcY7EbBGZIoGHzk54ZEnTrHbVtB0rPbu9cLIGxwTyumPlf0rQIDAQAB"
      + "AoGAOqmVgnqiuB5NAPtT+D1lYAsH9VHglh/zPOgwiXh18/w6wSTEZZPU4+DQcsBe"
      + "dHxcPSbpkt273xzHT4lsTsRe3lGnOmDDvjutFm45lECHHhebOFb56LNMJ1cXBuNb"
      + "/LFma9zXGUiz8MheIF1vLI8Vv7zQnWZwoA6m2ZhTquefr0ECQQDituijRRKurWGR"
      + "9zW+7eoq9FpYKMWT2knAd+pAbLrTxa+6dxr8O1rcnA2TqAln4EsK+1ICWw7nvr+Q"
      + "vlPa/nrRAkEAwpNYxhXrKG4LZfuXxW16kQHf3D8kVCU7S/AWiX/5xc7/UdScI1gv"
      + "A7utydzL7NSOZ5v3APrB7fLGA9HbE5AbHQJBANtlABpIFL0/geVcYwGCgRLSZr9b"
      + "rV/YZXjBUsrHrcCUVOtQqaef1N0akzTp7zg74MMe1Iz9C56OhBmRZdrOW5ECQQCg"
      + "H7xFv5H3gqA0vBjJa3AbV2yPhxLxgHZM5vbVDxFQ/SlWgV5BI/gE3XWGkwLrEn1i"
      + "vJJIJxRjKpBUYmYkskntAkBOgWldiubTraFBTA2p1jsQ900crTHakAkN8Rfx4LO8"
      + "4tF1wYPBggHv85w2sjmW77kkQt9nRuRVlbYAKKCvkRGX";

  /**
   * Generate random encryption key string.
   *
   * @param privateKeyForDecryption the private key for decryption
   * @param encRandomKey            the enc random key
   * @return the string
   */
  private static String getRandomDecryptionKey(String privateKeyForDecryption,
                                               String encRandomKey) {
    try {
      return EncryptionUtil.getDecryptedStringPrivateKey(encRandomKey,
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