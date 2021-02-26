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
    comments = "Source: ServerMessage.proto")
public final class ServerServiceGrpc {

  private ServerServiceGrpc() {}

  public static final String SERVICE_NAME = "ProtoMessage.ServerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest,
      org.mmo.message.ServerRegisterUpdateResponse> getServerRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ServerRegister",
      requestType = org.mmo.message.ServerRegisterUpdateRequest.class,
      responseType = org.mmo.message.ServerRegisterUpdateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest,
      org.mmo.message.ServerRegisterUpdateResponse> getServerRegisterMethod() {
    io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest, org.mmo.message.ServerRegisterUpdateResponse> getServerRegisterMethod;
    if ((getServerRegisterMethod = ServerServiceGrpc.getServerRegisterMethod) == null) {
      synchronized (ServerServiceGrpc.class) {
        if ((getServerRegisterMethod = ServerServiceGrpc.getServerRegisterMethod) == null) {
          ServerServiceGrpc.getServerRegisterMethod = getServerRegisterMethod =
              io.grpc.MethodDescriptor.<org.mmo.message.ServerRegisterUpdateRequest, org.mmo.message.ServerRegisterUpdateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ServerRegister"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerRegisterUpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerRegisterUpdateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerServiceMethodDescriptorSupplier("ServerRegister"))
              .build();
        }
      }
    }
    return getServerRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest,
      org.mmo.message.ServerRegisterUpdateResponse> getServerUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ServerUpdate",
      requestType = org.mmo.message.ServerRegisterUpdateRequest.class,
      responseType = org.mmo.message.ServerRegisterUpdateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest,
      org.mmo.message.ServerRegisterUpdateResponse> getServerUpdateMethod() {
    io.grpc.MethodDescriptor<org.mmo.message.ServerRegisterUpdateRequest, org.mmo.message.ServerRegisterUpdateResponse> getServerUpdateMethod;
    if ((getServerUpdateMethod = ServerServiceGrpc.getServerUpdateMethod) == null) {
      synchronized (ServerServiceGrpc.class) {
        if ((getServerUpdateMethod = ServerServiceGrpc.getServerUpdateMethod) == null) {
          ServerServiceGrpc.getServerUpdateMethod = getServerUpdateMethod =
              io.grpc.MethodDescriptor.<org.mmo.message.ServerRegisterUpdateRequest, org.mmo.message.ServerRegisterUpdateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ServerUpdate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerRegisterUpdateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerRegisterUpdateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerServiceMethodDescriptorSupplier("ServerUpdate"))
              .build();
        }
      }
    }
    return getServerUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.mmo.message.ServerListRequest,
      org.mmo.message.ServerListResponse> getServerListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ServerList",
      requestType = org.mmo.message.ServerListRequest.class,
      responseType = org.mmo.message.ServerListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mmo.message.ServerListRequest,
      org.mmo.message.ServerListResponse> getServerListMethod() {
    io.grpc.MethodDescriptor<org.mmo.message.ServerListRequest, org.mmo.message.ServerListResponse> getServerListMethod;
    if ((getServerListMethod = ServerServiceGrpc.getServerListMethod) == null) {
      synchronized (ServerServiceGrpc.class) {
        if ((getServerListMethod = ServerServiceGrpc.getServerListMethod) == null) {
          ServerServiceGrpc.getServerListMethod = getServerListMethod =
              io.grpc.MethodDescriptor.<org.mmo.message.ServerListRequest, org.mmo.message.ServerListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ServerList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerListRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mmo.message.ServerListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerServiceMethodDescriptorSupplier("ServerList"))
              .build();
        }
      }
    }
    return getServerListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<LoadScriptRequest,
		  LoadScriptResponse> getLoadScriptMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LoadScript",
      requestType = LoadScriptRequest.class,
      responseType = LoadScriptResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<LoadScriptRequest,
		  LoadScriptResponse> getLoadScriptMethod() {
    io.grpc.MethodDescriptor<LoadScriptRequest, LoadScriptResponse> getLoadScriptMethod;
    if ((getLoadScriptMethod = ServerServiceGrpc.getLoadScriptMethod) == null) {
      synchronized (ServerServiceGrpc.class) {
        if ((getLoadScriptMethod = ServerServiceGrpc.getLoadScriptMethod) == null) {
          ServerServiceGrpc.getLoadScriptMethod = getLoadScriptMethod =
              io.grpc.MethodDescriptor.<LoadScriptRequest, LoadScriptResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LoadScript"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  LoadScriptRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  LoadScriptResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerServiceMethodDescriptorSupplier("LoadScript"))
              .build();
        }
      }
    }
    return getLoadScriptMethod;
  }

  private static volatile io.grpc.MethodDescriptor<HttpRequest,
		  HttpResponse> getHttpPostMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "httpPost",
      requestType = HttpRequest.class,
      responseType = HttpResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<HttpRequest,
		  HttpResponse> getHttpPostMethod() {
    io.grpc.MethodDescriptor<HttpRequest, HttpResponse> getHttpPostMethod;
    if ((getHttpPostMethod = ServerServiceGrpc.getHttpPostMethod) == null) {
      synchronized (ServerServiceGrpc.class) {
        if ((getHttpPostMethod = ServerServiceGrpc.getHttpPostMethod) == null) {
          ServerServiceGrpc.getHttpPostMethod = getHttpPostMethod =
              io.grpc.MethodDescriptor.<HttpRequest, HttpResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "httpPost"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  HttpRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  HttpResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerServiceMethodDescriptorSupplier("httpPost"))
              .build();
        }
      }
    }
    return getHttpPostMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ServerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerServiceStub>() {
        @java.lang.Override
        public ServerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerServiceStub(channel, callOptions);
        }
      };
    return ServerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ServerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerServiceBlockingStub>() {
        @java.lang.Override
        public ServerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerServiceBlockingStub(channel, callOptions);
        }
      };
    return ServerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ServerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerServiceFutureStub>() {
        @java.lang.Override
        public ServerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerServiceFutureStub(channel, callOptions);
        }
      };
    return ServerServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static abstract class ServerServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public void serverRegister(org.mmo.message.ServerRegisterUpdateRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getServerRegisterMethod(), responseObserver);
    }

    /**
     * <pre>
     *服务器更新
     * </pre>
     */
    public void serverUpdate(org.mmo.message.ServerRegisterUpdateRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getServerUpdateMethod(), responseObserver);
    }

    /**
     * <pre>
     *获取服务器列表
     * </pre>
     */
    public void serverList(org.mmo.message.ServerListRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getServerListMethod(), responseObserver);
    }

    /**
     * <pre>
     *加载脚本
     * </pre>
     */
    public void loadScript(LoadScriptRequest request,
        io.grpc.stub.StreamObserver<LoadScriptResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLoadScriptMethod(), responseObserver);
    }

    /**
     * <pre>
     *http请求
     * </pre>
     */
    public void httpPost(HttpRequest request,
        io.grpc.stub.StreamObserver<HttpResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHttpPostMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getServerRegisterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mmo.message.ServerRegisterUpdateRequest,
                org.mmo.message.ServerRegisterUpdateResponse>(
                  this, METHODID_SERVER_REGISTER)))
          .addMethod(
            getServerUpdateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mmo.message.ServerRegisterUpdateRequest,
                org.mmo.message.ServerRegisterUpdateResponse>(
                  this, METHODID_SERVER_UPDATE)))
          .addMethod(
            getServerListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mmo.message.ServerListRequest,
                org.mmo.message.ServerListResponse>(
                  this, METHODID_SERVER_LIST)))
          .addMethod(
            getLoadScriptMethod(),
            asyncUnaryCall(
              new MethodHandlers<
		              LoadScriptRequest,
		              LoadScriptResponse>(
                  this, METHODID_LOAD_SCRIPT)))
          .addMethod(
            getHttpPostMethod(),
            asyncUnaryCall(
              new MethodHandlers<
		              HttpRequest,
		              HttpResponse>(
                  this, METHODID_HTTP_POST)))
          .build();
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class ServerServiceStub extends io.grpc.stub.AbstractAsyncStub<ServerServiceStub> {
    private ServerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public void serverRegister(org.mmo.message.ServerRegisterUpdateRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getServerRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *服务器更新
     * </pre>
     */
    public void serverUpdate(org.mmo.message.ServerRegisterUpdateRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getServerUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *获取服务器列表
     * </pre>
     */
    public void serverList(org.mmo.message.ServerListRequest request,
        io.grpc.stub.StreamObserver<org.mmo.message.ServerListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getServerListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *加载脚本
     * </pre>
     */
    public void loadScript(LoadScriptRequest request,
        io.grpc.stub.StreamObserver<LoadScriptResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLoadScriptMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *http请求
     * </pre>
     */
    public void httpPost(HttpRequest request,
        io.grpc.stub.StreamObserver<HttpResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHttpPostMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class ServerServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ServerServiceBlockingStub> {
    private ServerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public org.mmo.message.ServerRegisterUpdateResponse serverRegister(org.mmo.message.ServerRegisterUpdateRequest request) {
      return blockingUnaryCall(
          getChannel(), getServerRegisterMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *服务器更新
     * </pre>
     */
    public org.mmo.message.ServerRegisterUpdateResponse serverUpdate(org.mmo.message.ServerRegisterUpdateRequest request) {
      return blockingUnaryCall(
          getChannel(), getServerUpdateMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *获取服务器列表
     * </pre>
     */
    public org.mmo.message.ServerListResponse serverList(org.mmo.message.ServerListRequest request) {
      return blockingUnaryCall(
          getChannel(), getServerListMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *加载脚本
     * </pre>
     */
    public LoadScriptResponse loadScript(LoadScriptRequest request) {
      return blockingUnaryCall(
          getChannel(), getLoadScriptMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *http请求
     * </pre>
     */
    public HttpResponse httpPost(HttpRequest request) {
      return blockingUnaryCall(
          getChannel(), getHttpPostMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *rpc
   * </pre>
   */
  public static final class ServerServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ServerServiceFutureStub> {
    private ServerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器注册
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mmo.message.ServerRegisterUpdateResponse> serverRegister(
        org.mmo.message.ServerRegisterUpdateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getServerRegisterMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *服务器更新
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mmo.message.ServerRegisterUpdateResponse> serverUpdate(
        org.mmo.message.ServerRegisterUpdateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getServerUpdateMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *获取服务器列表
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mmo.message.ServerListResponse> serverList(
        org.mmo.message.ServerListRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getServerListMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *加载脚本
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<LoadScriptResponse> loadScript(
        LoadScriptRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLoadScriptMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *http请求
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<HttpResponse> httpPost(
        HttpRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHttpPostMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SERVER_REGISTER = 0;
  private static final int METHODID_SERVER_UPDATE = 1;
  private static final int METHODID_SERVER_LIST = 2;
  private static final int METHODID_LOAD_SCRIPT = 3;
  private static final int METHODID_HTTP_POST = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ServerServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ServerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SERVER_REGISTER:
          serviceImpl.serverRegister((org.mmo.message.ServerRegisterUpdateRequest) request,
              (io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse>) responseObserver);
          break;
        case METHODID_SERVER_UPDATE:
          serviceImpl.serverUpdate((org.mmo.message.ServerRegisterUpdateRequest) request,
              (io.grpc.stub.StreamObserver<org.mmo.message.ServerRegisterUpdateResponse>) responseObserver);
          break;
        case METHODID_SERVER_LIST:
          serviceImpl.serverList((org.mmo.message.ServerListRequest) request,
              (io.grpc.stub.StreamObserver<org.mmo.message.ServerListResponse>) responseObserver);
          break;
        case METHODID_LOAD_SCRIPT:
          serviceImpl.loadScript((LoadScriptRequest) request,
              (io.grpc.stub.StreamObserver<LoadScriptResponse>) responseObserver);
          break;
        case METHODID_HTTP_POST:
          serviceImpl.httpPost((HttpRequest) request,
              (io.grpc.stub.StreamObserver<HttpResponse>) responseObserver);
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

  private static abstract class ServerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ServerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.mmo.message.ServerMessage.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ServerService");
    }
  }

  private static final class ServerServiceFileDescriptorSupplier
      extends ServerServiceBaseDescriptorSupplier {
    ServerServiceFileDescriptorSupplier() {}
  }

  private static final class ServerServiceMethodDescriptorSupplier
      extends ServerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ServerServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ServerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ServerServiceFileDescriptorSupplier())
              .addMethod(getServerRegisterMethod())
              .addMethod(getServerUpdateMethod())
              .addMethod(getServerListMethod())
              .addMethod(getLoadScriptMethod())
              .addMethod(getHttpPostMethod())
              .build();
        }
      }
    }
    return result;
  }
}
