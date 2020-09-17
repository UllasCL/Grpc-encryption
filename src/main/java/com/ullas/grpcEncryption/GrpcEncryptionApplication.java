package com.ullas.grpcEncryption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The type Grpc encryption application.
 */
@SpringBootApplication
@Configuration
@ComponentScan
public class GrpcEncryptionApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GrpcEncryptionApplication.class, args);
	}
}
