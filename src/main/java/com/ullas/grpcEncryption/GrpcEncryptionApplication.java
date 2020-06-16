package com.ullas.grpcEncryption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The type Grpc encryption application.
 */
@SpringBootApplication
public class GrpcEncryptionApplication extends SpringBootServletInitializer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(GrpcEncryptionApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    return builder.sources(GrpcEncryptionApplication.class);
  }
}
