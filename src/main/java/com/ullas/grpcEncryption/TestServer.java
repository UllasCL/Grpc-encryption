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
      = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDKNvKr2Q4dQAoG"
      + "2K6iku9mPIRFjq/+WtQXHVvzY2YVHaT9pr/S8XfK+Ego7dn24PCZiSZAT52gz0pq"
      + "1XWgCqr11AQWYn7yi2BpxLsLhEFXvV0j0C4Qnxc0xhbwNgjOgbuz0ix9r7YdyLdt"
      + "yivywwnSlIdg36IjfpaESYmIR/y8XQX4lQrdEHHL5e19kdm+TXAgPf+h74vzJIxW"
      + "gQEAqziMyxXU9Y9ElyZnAixDDneWQvxm9dhFvCOiNyInfer4rEHcooxw7GvGIdpH"
      + "vRKtF1kJCxYcmQIRJlGZNXu2BY0ejQljFBn3Zvr9Z/ugHIg/zaZXfp7l8R89/fs1"
      + "NLftvJfRAgMBAAECggEAd9Qf8d3qE0NDrdLxib1wyDDMYTuw5xWD4T6wooYglBrB"
      + "Dt6lhac5yblfaYGpTDb33MP9OtbzHbw46R4dWf14WbyNv4YWnDwjognGnYh+ADRi"
      + "4ToYUhk0cz1/klEy3szaIjFF1CUv0m6C5DiN2onhZDxIDQv41PfK0hGRwoSGLEgV"
      + "/Tnr3mgmC/VC9or8kiopXgNoGWEuPaKSqSVzxHaxGpqichznbSuhLnj7hhIMjEse"
      + "az/D36e3E0uYOytFxyTlPcnBdeFXuZI9KoElg3f6vSKYIBDlTrJIucwA5F1TjZE6"
      + "cmPXGGuJWM3qt9HWacGLLST/b6pnjQZhPEqSxgkzUQKBgQDkWgsX0hT/zo8RQO6z"
      + "YVK+9oItjjzx+m44JRq7RqE9b+c8zHjY9vijtTm2bSQSDdlgxKXDibZUgYK2/GJk"
      + "bvIB84rsXaLNLFdpfYm187BxxHY7YcQcR4spEOWhpODVX/bIq8TPZafCswwJ1FOL"
      + "DaTyhtkAQILj+gInvqFudd+vnwKBgQDissNewEiXzLI65Y9GVtFTKZjDultaxuYo"
      + "PZSX+uLk7e5+RaMefjhmoDVKkritUMedphGmJ2SbnNMIv1kCAAUalUb8Y733wXXq"
      + "VpCYyOwmLEMUxeYvfJ3FwpQtmJqbP60PdufedjPAbCq4TcxtrnRlkMjDc1jTkRhk"
      + "rTWptG3CjwKBgF7xtWUNG2AVEVU6K0V4NMBjnFK2rk5qMsOzYb72KfpyebHEzoZf"
      + "gZgHtNxN2s6K5ZQfj1CTBLV0N9Y/b+WUX5lCGsQWVqd6RUn+QXDexsE2z3X3O2QK"
      + "tyi29tNTy4vMX76l9KnK+YqxiVLY7neFj8yrFrTNHd8ORzEhSNu1tPC/AoGBALnA"
      + "yJ0m6VSZAaGI4DE+ih6egvI+DQUA3S4z50Hw7WGwJC+LV/Fwcz6EH6aPOoupGSdw"
      + "IB4JQULQ6YrNX11oLsZtctDz2YDdIIV3kNvgJr/QnNa0obxfW9fjbA9Ab86uClwV"
      + "jkCWkikleIu0E8H7pJxkWlzscuhIh7hWxRXdJeK5AoGAV99tnIFs6RlD3Kw6mF3g"
      + "59WTTTVbp/IvXcmtwccRy9CyhoHbZMxpnlTjplKfIM0jrX/Xv6Kjq3PQ0wSvI5Hv"
      + "673er8EadgGFKdzt8uMCTvvkie6u+Xy7YYLEyFjJPhlaVNe0PGcKjqyzyOIT/NfT"
      + "tS/cOzmRqFkBIvetAbSObuk=";

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