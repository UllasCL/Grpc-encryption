package com.ullas.grpcEncryption.interceptors;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The type Grpc decryption interceptor.
 */
@Component
@Order(-2147483647)
public class GrpcDecryptionInterceptor implements ServerInterceptor {
  /**
   * The constant LOGGER.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcDecryptionInterceptor.class);
  /**
   * The constant NOOP_LISTENER.
   */
  private static final ServerCall.Listener NOOP_LISTENER = new ServerCall.Listener() {
  };

  /**
   * Instantiates a new Grpc decryption interceptor.
   */
  public GrpcDecryptionInterceptor() {
  }

  /**
   * Intercept call server call . listener.
   *
   * @param <ReqT>  the type parameter
   * @param <RespT> the type parameter
   * @param call    the call
   * @param headers the headers
   * @param next    the next
   * @return the server call . listener
   */
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                               final Metadata headers,
                                                               final ServerCallHandler<ReqT,
                                                                   RespT> next) {
    try {
      LOGGER.info("Inside interceptCall",call);

      return Contexts.interceptCall(Context.current(), call, headers, next);
    } catch (StatusRuntimeException var6) {
      LOGGER.error("Error inside jwt interceptor", var6);
      call.close(var6.getStatus(), headers);
      return NOOP_LISTENER;
    } catch (Exception var7) {
      LOGGER.error("Error inside jwt interceptor", var7);
      call.close(Status.INTERNAL.withDescription(var7.getMessage()).withCause(var7), headers);
      return NOOP_LISTENER;
    }
  }
}
