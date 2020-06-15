package com.ullas.grpcEncryption.interceptors;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(-2147483648)
public class GrpcServerJwtInterceptor implements ServerInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerJwtInterceptor.class);
  private static final Listener NOOP_LISTENER = new Listener() {
  };

  public GrpcServerJwtInterceptor() {
  }

  public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                    final Metadata headers,
                                                    final ServerCallHandler<ReqT,
                                                        RespT> next) {
    try {
      LOGGER.info("Inside interceptCall");

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
