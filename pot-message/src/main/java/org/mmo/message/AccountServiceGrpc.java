package org.mmo.message;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 *rpc
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.27.0)",
    comments = "Source: AccountMessage.proto")
public final class AccountServiceGrpc {

  private AccountServiceGrpc() {}

  public static final String SERVICE_NAME = "ProtoMessage.AccountService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.mmo.message.LoginRequest,
      org.mmo.message.LoginResponse> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "login",
      requestType = org.mmo.message.LoginRequest.class,
      responseType = org.mmo.message.LoginResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mmo.message.LoginRequest,
      org.mmo.message.LoginResponse> getLoginMethod() {
    io.grpc.MethodDescriptor<org.mmo.message.LoginRequest, org.mmo.message.LoginResponse> getLoginMethod;
    if ((getLoginMethod = AccountServiceGrpc.getLoginMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getLoginMethod = AccountServiceGrpc.getLoginMethod) == null) {
          AccountServiceGrpc.getLoginMethod = getLoginMethod =
              io.grpc.MethodDescriptor.<org.mmo.message.LoginRequest, org.mmo.message.LoginResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.LoginResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("login"))
              .build();
        }
      }
    }
    return getLoginMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccountServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub>() {
        @java.lang.Override
        public AccountServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceStub(channel, callOptions);
        }
      };
    return AccountServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccountServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub>() {
        @java.lang.Override
        public AccountServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceBlockingStub(channel, callOptions);
        }
      };
    return AccountServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccountServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub>() {
        @java.lang.Override
        public AccountServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceFutureStub(channel, callOptions);
        }
      };
    return AccountServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static abstract class AccountServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public void login(org.mmo.message.LoginRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.LoginResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getLoginMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mmo.message.LoginRequest,
                org.mmo.message.LoginResponse>(
                  this, METHODID_LOGIN)))
          .build();
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class AccountServiceStub extends io.grpc.stub.AbstractAsyncStub<AccountServiceStub> {
    private AccountServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public void login(org.mmo.message.LoginRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.LoginResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class AccountServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<AccountServiceBlockingStub> {
    private AccountServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public org.mmo.message.LoginResponse login(org.mmo.message.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class AccountServiceFutureStub extends io.grpc.stub.AbstractFutureStub<AccountServiceFutureStub> {
    private AccountServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mmo.message.LoginResponse> login(
        org.mmo.message.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOGIN = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AccountServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AccountServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOGIN:
          serviceImpl.login((org.mmo.message.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.mmo.message.LoginResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccountServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return AccountMessage.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AccountService");
    }
  }

  private static final class AccountServiceFileDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier {
    AccountServiceFileDescriptorSupplier() {}
  }

  private static final class AccountServiceMethodDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AccountServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AccountServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccountServiceFileDescriptorSupplier())
              .addMethod(getLoginMethod())
              .build();
        }
      }
    }
    return result;
  }
}
