package com.ullas.grpcEncryption.interceptors;

import com.ullas.grpcEncryption.utils.ModifyRequestUtil;
import com.ullas.grpcEncryption.utils.ModifyResponseUtil;
import com.ullas.grpcEncryption.utils.RandomUtil;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final Listener NOOP_LISTENER = new Listener() {
  };

  /**
   * The Modify response util.
   */
  @Autowired
  private ModifyResponseUtil modifyResponseUtil;
  /**
   * The Modify request util.
   */
  @Autowired
  private ModifyRequestUtil modifyRequestUtil;

  /**
   * The Random util.
   */
  @Autowired
  private RandomUtil randomUtil;

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
  public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                    final Metadata headers,
                                                    final ServerCallHandler<ReqT,
                                                        RespT> next) {
    try {
      var grpcServerCall = new GrpcServerCall(call);
      var listener = next.startCall(grpcServerCall, headers);
      return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
        @Override
        public void onMessage(ReqT message) {
          ReqT modifiedMessage = modifyRequestUtil.modifyRequest(message);
          super.onMessage(modifiedMessage);
        }
      };
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

  /**
   * The type Grpc server call.
   *
   * @param <M> the type parameter
   * @param <R> the type parameter
   */
  private class GrpcServerCall<M, R> extends ServerCall<M, R> {

    /**
     * The Server call.
     */
    ServerCall<M, R> serverCall;

    /**
     * Instantiates a new Grpc server call.
     *
     * @param serverCall the server call
     */
    protected GrpcServerCall(ServerCall<M, R> serverCall) {
      this.serverCall = serverCall;
    }

    /**
     * Request.
     *
     * @param numMessages the num messages
     */
    @Override
    public void request(int numMessages) {
      serverCall.request(numMessages);
    }

    /**
     * Send headers.
     *
     * @param headers the headers
     */
    @Override
    public void sendHeaders(Metadata headers) {
      serverCall.sendHeaders(headers);
    }

    /**
     * Send message.
     *
     * @param message the message
     */
    @Override
    public void sendMessage(R message) {
      LOGGER.info("Method: {}, Response: {}", serverCall.getMethodDescriptor().getFullMethodName(),
          message);
      R modifiedResponse = modifyResponseUtil.modifyResponse(message);
      serverCall.sendMessage(modifiedResponse);
    }

    /**
     * Close.
     *
     * @param status   the status
     * @param trailers the trailers
     */
    @Override
    public void close(Status status, Metadata trailers) {
      serverCall.close(status, trailers);
    }

    /**
     * Is cancelled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isCancelled() {
      return serverCall.isCancelled();
    }

    /**
     * Gets method descriptor.
     *
     * @return the method descriptor
     */
    @Override
    public MethodDescriptor<M, R> getMethodDescriptor() {
      return serverCall.getMethodDescriptor();
    }
  }
}
