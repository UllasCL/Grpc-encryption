package com.ullas.grpcEncryption.interceptors;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import com.google.protobuf.ByteString;
import com.ullas.grpcEncryption.EncryptedMessage;
import com.ullas.grpcEncryption.utils.AesCryptUtil;
import com.ullas.grpcEncryption.utils.RandomUtils;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The type Grpc decryption interceptor.
 */
@Component
@Order(-2147483647)
public class GrpcDecryptionInterceptor implements ServerInterceptor {

  /**
   * The constant TRACE_ID_KEY.
   */
  public static final Metadata.Key<String> TRACE_ID_KEY = Metadata.Key.of("traceId",
      ASCII_STRING_MARSHALLER);
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
   * The constant private_key.
   */
  private static String private_key
      = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcaR2NGQ9PoDUz"
      + "fFKW74XU/szReZRXhHh9zMaOhhRgzzi+Vg3aJnAg4FoohBTCtvrbRtHEbFw5uyVL"
      + "DDkUY3J78qMQTuoYCh3V33l52YW/sLjyEbu/ahkAgZKK+DnIt56Zn84gFjCk6POA"
      + "ZYIfe1iomXkKJh3C/wMlNJ9V1Z7/sgQsS7S7iR6GlxF1DnlTY5Hnvj7bQLlE3R6x"
      + "L1VvuhdheCUWx8WXxV2vZkk3KMdC8gNNNkw5j+7ncHo2Cc4R7LfFYuwx91XRpelT"
      + "sDz0PkExsu4FPkHlEzxbLXOER4a3PmU2Da4B6bft12KP4T6KeGrJX3ZlpBpri1Op"
      + "xji2B1oLAgMBAAECggEBALTCVCTqP19LATaHJ+h26SgECw9kCR85GeP5s4EhPsci"
      + "SuP2CQg2a9DajbYalDgX/mUkkFXplD0YWP5SOOGaqUIzfD2cqmkqqstCqgofNYw+"
      + "r35+wp25smVy3i6wYhtVU3Gj2FUCPcSQ81oCZZPSAaTLsLN9DP1lTDwQCvhG6E3D"
      + "N/EjFWtTqoInimN6qx4EjHwmMoxAZv+tXNl74L/+EbSfmNPwTYaSMpLJ2RryZ0cN"
      + "NRkkEPuVlb58sazHdgRXndVCRGWYxrYVnGsdRAEFzM6AcTBBfpL0p6sQ7bqpemBt"
      + "8y58AlMbd9nbGBL4JjBTv5EtE4CET17bbzNHx12t7YECgYEA8bmdAaadk6x/tIHx"
      + "Pm29DVbjjHiJgCBwBsx4tXn/z9wqGFL9K/vTAZWiNZ7CK4GaJrqeEknsw9Ec0GlU"
      + "JnyIYSmi8OOFHMSeUn3SWbLlOcSzy7t5zpcArgasXc1hW4tTGKJU6U12rI0NAhh/"
      + "j63ByNCNQ0pjB/UZg5c43qMfMUECgYEA6W1FfslCLuR3CAAd9yr7rJOzCBIjqtOY"
      + "aSvCq2ROwTuJuVs7b0QlVowHlq9k+T9DBvxqKE4OuKjBX+/J8jsqx9w1OJ81Ls0s"
      + "sOZxlbOpfMRZklJKPZ+IsOu1mm8oLf3h9LOK9oVpEiA0KO3FYypXjZYgo6F/P+WM"
      + "buhQP4987EsCgYEAuTY2RfiLDoaChyV1WhctXtYvngcRm/m+vIbZCnwC9RpFqsOT"
      + "tqrKP5GOazVMo5c3LOuaiHraDiPgxxOdZavPt1r2vUonSSaiBKThktcO6hd8h+MK"
      + "7q8m1zDHy0u9iC4Vqm9fTi+LeaTNzchFnSpOq/aX9nI9tPAo73nkGjq0gMECgYBJ"
      + "NyjxF5dWtfpP3BmKCUVfYARrp6TgU/YeJsesko3RNieAqAkAYVOEze8jaGg3dYxE"
      + "iK+TbMhZSzU8Li4deEl23q8iz20S/O/jnKRL3EpxA/VEW+NFaOCq/YO8qNjldBiH"
      + "dDlGEZpfQRKbM9kRvZowosIiEOmk1Q8zSlLBXPRF+QKBgFa07I4/MtQ9TWURlZFf"
      + "d9CZNCZBjgL/bYl9sPnEtssLT3Tla1sr3td2PLCf7RzxQJPzz0wic3aE95oaUww8"
      + "ffphtU8gzKEYSBUzYnV7U9HKsFIV1bgewlovji92VqG/Rsb8SBgVLerxuEf+N+Ya"
      + "YnUV8vEb+baekebanXMPnK38";

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
      return new GrpcForwardingServerCallListener<ReqT>(call.getMethodDescriptor(), listener) {
        @Override
        public void onMessage(ReqT message) {

          ReqT modifiedMessage = modify(message);
          delegate().onMessage(modifiedMessage);

          LOGGER.info("Method: {}, Message: {}", methodName, message);
          super.onMessage(message);
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
   * Modify req t.
   *
   * @param <ReqT>  the type parameter
   * @param message the message
   * @return the req t
   */
  private <ReqT> ReqT modify(final ReqT message) {
    String secretkey = RandomUtils.getRandomDecryptionKey(
        ((EncryptedMessage) message).getKey().toByteArray());
    byte[] decBytes = null;
    try {
      decBytes =
          AesCryptUtil.decrypt(((EncryptedMessage) message).getData().toByteArray(),
              secretkey);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assert decBytes != null;
    return (ReqT) EncryptedMessage.newBuilder().setData(ByteString.copyFrom(decBytes))
        .build();

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
      serverCall.sendMessage(message);
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

  /**
   * The type Grpc forwarding server call listener.
   *
   * @param <ReqT> the type parameter
   */
  private class GrpcForwardingServerCallListener<ReqT>
      extends io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
    /**
     * The Method name.
     */
    String methodName;

    /**
     * Instantiates a new Grpc forwarding server call listener.
     *
     * @param method   the method
     * @param listener the listener
     */
    protected GrpcForwardingServerCallListener(MethodDescriptor method,
                                               Listener<ReqT> listener) {
      super(listener);
      methodName = method.getFullMethodName();
    }
  }
}
