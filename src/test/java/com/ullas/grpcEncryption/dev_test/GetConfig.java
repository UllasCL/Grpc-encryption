package com.ullas.grpcEncryption.dev_test;

import com.ullas.grpcEncryption.EncryptedMessage;
import com.ullas.grpcEncryption.config.BaseTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The type Get config.
 */
@SpringBootTest
public class GetConfig extends BaseTest {

  @Test
  public void getConfig(){
    EncryptedMessage response  = testServiceBlockingStub.getConfig(
        EncryptedMessage.newBuilder()
            .build());
  }
}
