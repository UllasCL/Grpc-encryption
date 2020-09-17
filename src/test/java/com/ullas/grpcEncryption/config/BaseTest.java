package com.ullas.grpcEncryption.config;

import com.ullas.grpcEncryption.TestServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;

/**
 * The type Base test.
 */
public class BaseTest {

  /**
   * The constant LOCALHOST_ENDPOINT.
   */
  public static final String LOCALHOST_ENDPOINT = "localhost:6565";
  /**
   * The Payment service blocking stub.
   */
  protected TestServiceGrpc.TestServiceBlockingStub
      testServiceBlockingStub;

  /**
   * Sets .
   */
  @Before
  public void setup() {
    testServiceBlockingStub =
        TestServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forTarget(
                LOCALHOST_ENDPOINT).usePlaintext()
                .build());
  }
}
