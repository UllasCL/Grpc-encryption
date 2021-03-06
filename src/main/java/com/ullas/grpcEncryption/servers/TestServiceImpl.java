package com.ullas.grpcEncryption.servers;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessage;
import com.ullas.grpcEncryption.Request1;
import com.ullas.grpcEncryption.Response1;
import com.ullas.grpcEncryption.TestServiceGrpc;
import com.ullas.grpcEncryption.interceptors.GrpcDecryptionInterceptor;
import io.grpc.stub.StreamObserver;
import java.util.Objects;
import org.lognet.springboot.grpc.GRpcService;

/**
 * The type Test service.
 */
@GRpcService(interceptors = {GrpcDecryptionInterceptor.class})
class TestServiceImpl extends TestServiceGrpc.TestServiceImplBase {
  /**
   * Gets config.
   *
   * @param request          the request
   * @param responseObserver the response observer
   */
  @Override
  public void getConfig(final EncryptedMessage request,
                        final StreamObserver<EncryptedMessage> responseObserver) {
    Response1 response;
    Request1 reqFromClient = null;
    try {
      reqFromClient =
          Request1.parseFrom(request.getData().toByteArray());
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
          ByteString.copyFrom(response.toByteArray()))
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
}