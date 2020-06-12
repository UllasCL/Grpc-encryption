package com.ullas.grpcEncryption;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ullas.grpcEncryption.GreeterGrpc.GreeterImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/**
 * The type Hello world server.
 */
public class HelloWorldServer {

  /**
   * The constant logger.
   */
  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());
  /**
   * The constant key.
   */
  private static final String key = "Bar12345Bar12345";

  /**
   * The Port.
   */
  private int port = 42423;
  /**
   * The Server.
   */
  private Server server;

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    logger.info("Server startup. Args = " + Arrays.toString(args));
    final HelloWorldServer helloWorldServer = new HelloWorldServer();

    helloWorldServer.start();
    helloWorldServer.blockUntilShutdown();
  }

  /**
   * Decrypt f string.
   *
   * @param encryptionBytes the encryption bytes
   * @param pkey            the pkey
   * @param c               the c
   * @return the string
   * @throws InvalidKeyException       the invalid key exception
   * @throws BadPaddingException       the bad padding exception
   * @throws IllegalBlockSizeException the illegal block size exception
   */
  private static String decryptF(byte[] encryptionBytes, Key pkey, Cipher c)
      throws InvalidKeyException,

             BadPaddingException, IllegalBlockSizeException {

    c.init(Cipher.DECRYPT_MODE, pkey);

    byte[] decrypt = c.doFinal(encryptionBytes);

    String decrypted = new String(decrypt);

    return decrypted;
  }

  /**
   * Decrypt string.
   *
   * @param <T>  the type parameter
   * @param data the data
   * @return the string
   * @throws Exception the exception
   */
  public static byte[] decrypt(byte[] data) throws Exception {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");
    // decrypt the text
    cipher.init(Cipher.DECRYPT_MODE, aesKey);
    return cipher.doFinal(data);
  }

  /**
   * Start.
   *
   * @throws Exception the exception
   */
  private void start() throws Exception {
    logger.info("Starting the grpc server");

    server = ServerBuilder.forPort(port)
        .addService(new GreeterImpl())
        .build()
        .start();

    logger.info("Server started. Listening on port " + port);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** JVM is shutting down. Turning off grpc server as well ***");
      HelloWorldServer.this.stop();
      System.err.println("*** shutdown complete ***");
    }));
  }

  /**
   * Stop.
   */
  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Block until shutdown.
   *
   * @throws InterruptedException the interrupted exception
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }


  /**
   * Encrypt data string.
   *
   * @param <T>  the type parameter
   * @param data the data
   * @return the string
   * @throws Exception the exception
   */
  public <T> String encryptData(T data) throws Exception {
    //Creating KeyPair generator object
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

    //Initializing the key pair generator
    keyPairGen.initialize(2048);

    //Generate the pair of keys
    KeyPair pair = keyPairGen.generateKeyPair();

    //Getting the public key from the key pair
    PublicKey publicKey = pair.getPublic();

    //Creating a Cipher object
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

    //Initializing a Cipher object
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);

    //Add data to the cipher
    byte[] input = data.toString().getBytes();
    cipher.update(input);

    //encrypting the data
    byte[] cipherText = cipher.doFinal();

    return new String(cipherText, "UTF8");
  }

  /**
   * The type Greeter.
   */
  private class GreeterImpl extends GreeterImplBase {

    /**
     * Say hello.
     *
     * @param request          the request
     * @param responseObserver the response observer
     */
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
      HelloResponse response = HelloResponse.newBuilder().setMessage("Hello " + request.getName())
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    /**
     * Say encrypted hello.
     *
     * @param request          the request
     * @param responseObserver the response observer
     */
    @Override
    public void sayEncryptedHello(EncryptedMessageReqRes request,
                                  StreamObserver<EncryptedMessageReqRes> responseObserver) {
      HelloResponse response;
      byte[] encReq2 = new byte[request.getPayload().toByteArray().length];

      for (int i = 0; i < request.getPayload().toByteArray().length; i++) {
        encReq2[i] = request.getPayload().toByteArray()[i];
      }
      HelloRequest reqFromClient = null;
      try {
        reqFromClient = HelloRequest.parseFrom(decrypt(request.getPayload().toByteArray()));
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
      EncryptedMessageReqRes encRes =
          EncryptedMessageReqRes.newBuilder().build();
      try {
        response = HelloResponse.newBuilder().setMessage(
            "hello " + reqFromClient.getName()
        ).build();
        encRes = EncryptedMessageReqRes.newBuilder().setPayload(
            ByteString.copyFrom(response.toByteArray()))
            .build();
      } catch (Exception e) {
        e.printStackTrace();
      }

      responseObserver.onNext(encRes);
      responseObserver.onCompleted();
    }
  }
}