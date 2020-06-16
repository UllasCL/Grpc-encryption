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
      = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcaR2NGQ9PoDUz"
      + "fFKW74XU/szReZRXhHh9zMaOhhRgzzi+Vg3aJnAg4FoohBTCtvrbRtHEbFw5uyVL"
      + "DDkUY3J78qMQTuoYCh3V33l52YW/sLjyEbu/ahkAgZKK+DnIt56Zn84gFjCk6POA"
      + "ZYIfe1iomXkKJh3C/wMlNJ9V1Z7/sgQsS7S7iR6GlxF1DnlTY5Hnvj7bQLlE3R6x"
      + "L1VvuhdheCUWx8WXxV2vZkk3KMdC8gNNNkw5j+7ncHo2Cc4R7LfFYuwx91XRpelT"
      + "sDz0PkExsu4FPkHlEzxbLXOER4a3PmU2Da4B6bft12KP4T6KeGrJX3ZlpBpri1Op"
      + "xji2B1oLAgMBAAECggEBALTCVCTqP19LATaHJ+h26SgECw9kCR85GeP5s4EhPsci"
      + "SuP2CQg2a9DajbYalDgX/mUkkFXplD0YWP5SOOGaqUIzfD2cqmkqqstCqgofNYw+"
      + "r35+wp25smVy3i6wYhtVU3Gj2FUCPcSQ81oCZZPSAaTLsLN9DP1lTDwQCvhG6E3D"
      + "N/EjFWtTqoInimN6qx4EjHwmMoxAZv+tXNl74L/+EbSfmNPwTYaSMpLJ2RryZ0cN"
      + "NRkkEPuVlb58sazHdgRXndVCRGWYxrYVnGsdRAEFzM6AcTBBfpL0p6sQ7bqpemBt"
      + "8y58AlMbd9nbGBL4JjBTv5EtE4CET17bbzNHx12t7YECgYEA8bmdAaadk6x/tIHx"
      + "Pm29DVbjjHiJgCBwBsx4tXn/z9wqGFL9K/vTAZWiNZ7CK4GaJrqeEknsw9Ec0GlU"
      + "JnyIYSmi8OOFHMSeUn3SWbLlOcSzy7t5zpcArgasXc1hW4tTGKJU6U12rI0NAhh/"
      + "j63ByNCNQ0pjB/UZg5c43qMfMUECgYEA6W1FfslCLuR3CAAd9yr7rJOzCBIjqtOY"
      + "aSvCq2ROwTuJuVs7b0QlVowHlq9k+T9DBvxqKE4OuKjBX+/J8jsqx9w1OJ81Ls0s"
      + "sOZxlbOpfMRZklJKPZ+IsOu1mm8oLf3h9LOK9oVpEiA0KO3FYypXjZYgo6F/P+WM"
      + "buhQP4987EsCgYEAuTY2RfiLDoaChyV1WhctXtYvngcRm/m+vIbZCnwC9RpFqsOT"
      + "tqrKP5GOazVMo5c3LOuaiHraDiPgxxOdZavPt1r2vUonSSaiBKThktcO6hd8h+MK"
      + "7q8m1zDHy0u9iC4Vqm9fTi+LeaTNzchFnSpOq/aX9nI9tPAo73nkGjq0gMECgYBJ"
      + "NyjxF5dWtfpP3BmKCUVfYARrp6TgU/YeJsesko3RNieAqAkAYVOEze8jaGg3dYxE"
      + "iK+TbMhZSzU8Li4deEl23q8iz20S/O/jnKRL3EpxA/VEW+NFaOCq/YO8qNjldBiH"
      + "dDlGEZpfQRKbM9kRvZowosIiEOmk1Q8zSlLBXPRF+QKBgFa07I4/MtQ9TWURlZFf"
      + "d9CZNCZBjgL/bYl9sPnEtssLT3Tla1sr3td2PLCf7RzxQJPzz0wic3aE95oaUww8"
      + "ffphtU8gzKEYSBUzYnV7U9HKsFIV1bgewlovji92VqG/Rsb8SBgVLerxuEf+N+Ya"
      + "YnUV8vEb+baekebanXMPnK38";

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