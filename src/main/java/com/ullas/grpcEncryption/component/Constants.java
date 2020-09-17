package com.ullas.grpcEncryption.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The type Constants.
 */
@Component
public class Constants {

  /**
   * The constant privateKey.
   */
  @Value("${aes.private_key}")
  public String privateKey;

  /**
   * Gets private key.
   *
   * @return the private key
   */
  public String getPrivateKey() {
    return privateKey;
  }

  /**
   * Sets private key.
   *
   * @param privateKey the private key
   */
  public void setPrivateKey(final String privateKey) {
    this.privateKey = privateKey;
  }
}
