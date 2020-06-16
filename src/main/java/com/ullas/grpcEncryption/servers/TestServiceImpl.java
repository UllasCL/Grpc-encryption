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
      = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkeqfp3JF/oZKT9cHBPWVIUCAqrXYvbCZmDGCQsgWJ0mO9rViDEjn+4Yqi5GkEcfkLsbFG66hgtlo9He1xiCPF0GsV97YAW8lL+qSEhG9DlNTEqK2LlRyhPqsF8DDQ+dy+7rk3O5A1ZzJg0GlMtqx1Se9p/W3ZqulNSDQkJ1AB+5Fpxmff/88eO8v4gbQII7nVEv28FdQ4S1LsA+8iGiTvTyGQ6t7mUgLlZy/C1W8LJz99CrtU8roZpTvCgtLEdv5AaYZFDNZ3s82ycDeAIBRDvBpfOe9lKeCdOLhUR1R5uRajJWN/CF/7XIh5sWkvX+NqYHL1+QP30e4WVNMFzba/AgMBAAECggEARvO+YYzLmyIo5/jY1zJT8ajW53IijHx76AOo2GJjLzv52MXha9E+AWxr8K7Vepxr0Zaaobxgyy9+p+S0HAidXeT1fOl5spflswfxVlUG5kFas2cMTi1Jx2kQP6SIem1+w0KZ1XC1WcQloEvnULdwl7wIMlGnxIEM2VCvZXUr915MEFg4FPOvGVR/+QPcf6Zi+XA/kTlHYwp3e8M9WrYhuJnaFIs6OsXoDmtg2MI3+Bv9Ddr9Gx3tdZumpsvV2qzbADMIE2PU4QaGygwVDsafZG1IrzAYnMPcZftSDjDy+DEwTAu7gm1tfhXTiyWKSdByO8C0xTmrzxLG4KWvnZ3BoQKBgQDT29Y3RbO2oxnyp6oAL7w8jMadVEhXb+9AR5OK1SA4YT43DlCFjfyF0R33Ex5Y5VQXFVikyGbNiQH0+Hszie2yDQ/mb74IvLN2gtr7SJOUrLGOHse6PZFyxVDPp8mzsVdH7m3ZvSkt/COwsdcieqwsjcIaVmQdhFfJ/YrpvMxeUQKBgQDGv60M9biICDUBybaOR5gjLPDGIvUP76IqHIOBnOSokiBuNvZAn9LZvQIG4/jkqsYd6Va6+QAmz/W9cdYss+WeVwcAh7JII+JcRQuDdKWykgcNMZj5jfOIOP+bJb/EEdPV2S6ZoTEw5YnfUJqPTF7GpKq6npmEnu/VA4fxD5UwDwKBgQCSy4Z4seE+MXEvQ6F1k53YTqroJJgjjbm3TmEbF2XF6wtS1qZQg/tvNWCP7D1g1zYW8IGFXB5vV/l1Jh8nrPB4KLjVsKN6JlYrTawx5S3tHGPC1+0ITNTVM4c0+0u1KLITc0/GSV2ZMVYBfphUma9nB6pw2V9xXFv/56AzKNBqoQKBgQDAG3f2xfCnNF+gKcyWfGd24LKeoh2EWNnDoKoQd4mh0xUb0251isaxBnvWEzd6OAOl1+yob5SQ74SVqFof/YdeZLp07bUK+/yJuYdmejLLh1DflOhLl8UlEAspePSCXcUHuJTNlLxKiiSZQX0UM/qMQZBxMGotgb9Kg8472RcYSwKBgGmOv4Xj7XoP7eTQu0AjJgPv/CGxuKFYxX1/ndA62zwTjXlVleRRCgVjgICA/1YScc/1+cfESGNzV+o0QM+a1drT7yTBQ6lDTCK3kuA9Sq82cnU8UkOQNfSuOA6TCpmeUDePOUiMtzJ/U0+BZUeKMsitanY9qZ9NvOufSOYEns6F";

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