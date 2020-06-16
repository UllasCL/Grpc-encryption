//package com.ullas.grpcEncryption;
//
//
//import com.google.protobuf.ByteString;
//import com.ullas.grpcEncryption.GreeterGrpc.GreeterImplBase;
//import com.ullas.grpcEncryption.utils.AesCryptUtil;
//import com.ullas.grpcEncryption.utils.AesEncryptionUtil;
//import com.ullas.grpcEncryption.utils.EncryptionUtil;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import io.grpc.stub.StreamObserver;
//import java.security.GeneralSecurityException;
//import java.security.KeyFactory;
//import java.security.PublicKey;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.logging.Logger;
//
///**
// * The type Hello world server.
// */
//public class HelloWorldServer {
//
//  /**
//   * The constant logger.
//   */
//  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());
//
//  /**
//   * The constant public_key.
//   */
//  private static String public_key = "";
//  /**
//   * The Port.
//   */
//  private int port = 42423;
//  /**
//   * The Server.
//   */
//  private Server server;
//
//  /**
//   * The entry point of application.
//   *
//   * @param args the input arguments
//   * @throws Exception the exception
//   */
//  public static void main(String[] args) throws Exception {
//    logger.info("Server startup. Args = " + Arrays.toString(args));
//    final HelloWorldServer helloWorldServer = new HelloWorldServer();
//
//    helloWorldServer.start();
//    helloWorldServer.blockUntilShutdown();
//  }
//
//  /**
//   * Generate random encryption key string.
//   *
//   * @param publicKeyForDecryption the public key for decryption
//   * @param encRendomKey           the enc rendom key
//   * @return the string
//   */
//  private static String getRandomDecryptionKey(String publicKeyForDecryption,
//                                               String encRendomKey) {
//    try {
//      return EncryptionUtil.getDecryptedString(encRendomKey,
//          getPublicKeyFromString(publicKeyForDecryption));
//    } catch (GeneralSecurityException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  /**
//   * Gets public key from string.
//   *
//   * @param stored the stored
//   * @return the public key from string
//   * @throws GeneralSecurityException the general security exception
//   */
//  private static PublicKey getPublicKeyFromString(String stored) throws GeneralSecurityException {
//    byte[] data = Base64.getDecoder().decode((stored.getBytes()));
//    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
//    KeyFactory fact = KeyFactory.getInstance("RSA");
//    return fact.generatePublic(spec);
//  }
//
//  /**
//   * Start.
//   *
//   * @throws Exception the exception
//   */
//  private void start() throws Exception {
//    logger.info("Starting the grpc server");
//
//    server = ServerBuilder.forPort(port)
//        .addService(new GreeterImpl())
//        .build()
//        .start();
//
//    logger.info("Server started. Listening on port " + port);
//
//    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//      System.err.println("*** JVM is shutting down. Turning off grpc server as well ***");
//      HelloWorldServer.this.stop();
//      System.err.println("*** shutdown complete ***");
//    }));
//  }
//
//  /**
//   * Stop.
//   */
//  private void stop() {
//    if (server != null) {
//      server.shutdown();
//    }
//  }
//
//  /**
//   * Block until shutdown.
//   *
//   * @throws InterruptedException the interrupted exception
//   */
//  private void blockUntilShutdown() throws InterruptedException {
//    if (server != null) {
//      server.awaitTermination();
//    }
//  }
//
//  /**
//   * The type Greeter.
//   */
//  private class GreeterImpl extends GreeterImplBase {
//
//    /**
//     * Say hello.
//     *
//     * @param request          the request
//     * @param responseObserver the response observer
//     */
//    @Override
//    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
//      HelloResponse response = HelloResponse.newBuilder().setMessage("Hello " + request.getName())
//          .build();
//      responseObserver.onNext(response);
//      responseObserver.onCompleted();
//    }
//
//    /**
//     * Say encrypted hello.
//     *
//     * @param request          the request
//     * @param responseObserver the response observer
//     */
//    @Override
//    public void sayEncryptedHello(EncryptedMessageReqRes request,
//                                  StreamObserver<EncryptedMessageReqRes> responseObserver) {
//      String secretkey = getRandomDecryptionKey(public_key, request.getEncRandomKey());
//      HelloResponse response;
//      HelloRequest reqFromClient = null;
//      try {
//        reqFromClient =
//            HelloRequest.parseFrom(AesCryptUtil.decrypt(request.getPayload().toByteArray(),
//                secretkey));
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      EncryptedMessageReqRes encRes =
//          EncryptedMessageReqRes.newBuilder().build();
//      try {
//        response = HelloResponse.newBuilder().setMessage(
//            "hello " + reqFromClient.getName()
//        ).build();
//        encRes = EncryptedMessageReqRes.newBuilder().setPayload(
//            ByteString.copyFrom(AesEncryptionUtil.encrypt(response.toByteArray())))
//            .build();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
//      responseObserver.onNext(encRes);
//      responseObserver.onCompleted();
//    }
//  }
//}