package com.ullas.grpcEncryption.utils;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * The type Random utils.
 */
public class RandomUtils {

  /**
   * The constant private_key.
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
   * Gets random decryption key.
   *
   * @param encRandomKey the enc random key
   * @return the random decryption key
   */
  public static String getRandomDecryptionKey(
      byte[] encRandomKey) {
    try {
      return EncryptionUtil.getDecryptedStringPrivateKey(encRandomKey,
          getPrivateKeyFromString(private_key));
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
  public static PrivateKey getPrivateKeyFromString(String stored) throws GeneralSecurityException {
    byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    PKCS8EncodedKeySpec spec =
        new PKCS8EncodedKeySpec(data);
    KeyFactory fact = KeyFactory.getInstance("RSA");
    return fact.generatePrivate(spec);
  }

  /**
   * Gets public key from string.
   *
   * @param stored the stored
   * @return the public key from string
   * @throws GeneralSecurityException the general security exception
   */
  public static PublicKey getPublicKeyFromString(String stored) throws GeneralSecurityException {
    byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    KeyFactory fact = KeyFactory.getInstance("RSA");
    return fact.generatePublic(spec);
  }
}
