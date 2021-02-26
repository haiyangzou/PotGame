// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ServerMessage.proto

package org.mmo.message;

/**
 * <pre>
 * 注册更新服务器
 * </pre>
 *
 * Protobuf type {@code ProtoMessage.ServerRegisterUpdateResponse}
 */
public  final class ServerRegisterUpdateResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ProtoMessage.ServerRegisterUpdateResponse)
    ServerRegisterUpdateResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ServerRegisterUpdateResponse.newBuilder() to construct.
  private ServerRegisterUpdateResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ServerRegisterUpdateResponse() {
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new ServerRegisterUpdateResponse();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ServerRegisterUpdateResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            org.mmo.message.ServerInfo.Builder subBuilder = null;
            if (serverInfo_ != null) {
              subBuilder = serverInfo_.toBuilder();
            }
            serverInfo_ = input.readMessage(org.mmo.message.ServerInfo.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(serverInfo_);
              serverInfo_ = subBuilder.buildPartial();
            }

            break;
          }
          case 16: {

            status_ = input.readInt32();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateResponse_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.mmo.message.ServerRegisterUpdateResponse.class, org.mmo.message.ServerRegisterUpdateResponse.Builder.class);
  }

  public static final int SERVERINFO_FIELD_NUMBER = 1;
  private org.mmo.message.ServerInfo serverInfo_;
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   * @return Whether the serverInfo field is set.
   */
  public boolean hasServerInfo() {
    return serverInfo_ != null;
  }
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   * @return The serverInfo.
   */
  public org.mmo.message.ServerInfo getServerInfo() {
    return serverInfo_ == null ? org.mmo.message.ServerInfo.getDefaultInstance() : serverInfo_;
  }
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   */
  public org.mmo.message.ServerInfoOrBuilder getServerInfoOrBuilder() {
    return getServerInfo();
  }

  public static final int STATUS_FIELD_NUMBER = 2;
  private int status_;
  /**
   * <pre>
   *0成功
   * </pre>
   *
   * <code>int32 status = 2;</code>
   * @return The status.
   */
  public int getStatus() {
    return status_;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (serverInfo_ != null) {
      output.writeMessage(1, getServerInfo());
    }
    if (status_ != 0) {
      output.writeInt32(2, status_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (serverInfo_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getServerInfo());
    }
    if (status_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, status_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.mmo.message.ServerRegisterUpdateResponse)) {
      return super.equals(obj);
    }
    org.mmo.message.ServerRegisterUpdateResponse other = (org.mmo.message.ServerRegisterUpdateResponse) obj;

    if (hasServerInfo() != other.hasServerInfo()) return false;
    if (hasServerInfo()) {
      if (!getServerInfo()
          .equals(other.getServerInfo())) return false;
    }
    if (getStatus()
        != other.getStatus()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasServerInfo()) {
      hash = (37 * hash) + SERVERINFO_FIELD_NUMBER;
      hash = (53 * hash) + getServerInfo().hashCode();
    }
    hash = (37 * hash) + STATUS_FIELD_NUMBER;
    hash = (53 * hash) + getStatus();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.mmo.message.ServerRegisterUpdateResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * 注册更新服务器
   * </pre>
   *
   * Protobuf type {@code ProtoMessage.ServerRegisterUpdateResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ProtoMessage.ServerRegisterUpdateResponse)
      org.mmo.message.ServerRegisterUpdateResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateResponse_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.mmo.message.ServerRegisterUpdateResponse.class, org.mmo.message.ServerRegisterUpdateResponse.Builder.class);
    }

    // Construct using org.mmo.message.ServerRegisterUpdateResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      if (serverInfoBuilder_ == null) {
        serverInfo_ = null;
      } else {
        serverInfo_ = null;
        serverInfoBuilder_ = null;
      }
      status_ = 0;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateResponse_descriptor;
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateResponse getDefaultInstanceForType() {
      return getDefaultInstance();
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateResponse build() {
      org.mmo.message.ServerRegisterUpdateResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateResponse buildPartial() {
      org.mmo.message.ServerRegisterUpdateResponse result = new org.mmo.message.ServerRegisterUpdateResponse(this);
      if (serverInfoBuilder_ == null) {
        result.serverInfo_ = serverInfo_;
      } else {
        result.serverInfo_ = serverInfoBuilder_.build();
      }
      result.status_ = status_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.mmo.message.ServerRegisterUpdateResponse) {
        return mergeFrom((org.mmo.message.ServerRegisterUpdateResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.mmo.message.ServerRegisterUpdateResponse other) {
      if (other == getDefaultInstance()) return this;
      if (other.hasServerInfo()) {
        mergeServerInfo(other.getServerInfo());
      }
      if (other.getStatus() != 0) {
        setStatus(other.getStatus());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.mmo.message.ServerRegisterUpdateResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.mmo.message.ServerRegisterUpdateResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private org.mmo.message.ServerInfo serverInfo_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.mmo.message.ServerInfo, org.mmo.message.ServerInfo.Builder, org.mmo.message.ServerInfoOrBuilder> serverInfoBuilder_;
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     * @return Whether the serverInfo field is set.
     */
    public boolean hasServerInfo() {
      return serverInfoBuilder_ != null || serverInfo_ != null;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     * @return The serverInfo.
     */
    public org.mmo.message.ServerInfo getServerInfo() {
      if (serverInfoBuilder_ == null) {
        return serverInfo_ == null ? org.mmo.message.ServerInfo.getDefaultInstance() : serverInfo_;
      } else {
        return serverInfoBuilder_.getMessage();
      }
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public Builder setServerInfo(org.mmo.message.ServerInfo value) {
      if (serverInfoBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        serverInfo_ = value;
        onChanged();
      } else {
        serverInfoBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public Builder setServerInfo(
        org.mmo.message.ServerInfo.Builder builderForValue) {
      if (serverInfoBuilder_ == null) {
        serverInfo_ = builderForValue.build();
        onChanged();
      } else {
        serverInfoBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public Builder mergeServerInfo(org.mmo.message.ServerInfo value) {
      if (serverInfoBuilder_ == null) {
        if (serverInfo_ != null) {
          serverInfo_ =
            org.mmo.message.ServerInfo.newBuilder(serverInfo_).mergeFrom(value).buildPartial();
        } else {
          serverInfo_ = value;
        }
        onChanged();
      } else {
        serverInfoBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public Builder clearServerInfo() {
      if (serverInfoBuilder_ == null) {
        serverInfo_ = null;
        onChanged();
      } else {
        serverInfo_ = null;
        serverInfoBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public org.mmo.message.ServerInfo.Builder getServerInfoBuilder() {
      
      onChanged();
      return getServerInfoFieldBuilder().getBuilder();
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    public org.mmo.message.ServerInfoOrBuilder getServerInfoOrBuilder() {
      if (serverInfoBuilder_ != null) {
        return serverInfoBuilder_.getMessageOrBuilder();
      } else {
        return serverInfo_ == null ?
            org.mmo.message.ServerInfo.getDefaultInstance() : serverInfo_;
      }
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.mmo.message.ServerInfo, org.mmo.message.ServerInfo.Builder, org.mmo.message.ServerInfoOrBuilder> 
        getServerInfoFieldBuilder() {
      if (serverInfoBuilder_ == null) {
        serverInfoBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.mmo.message.ServerInfo, org.mmo.message.ServerInfo.Builder, org.mmo.message.ServerInfoOrBuilder>(
                getServerInfo(),
                getParentForChildren(),
                isClean());
        serverInfo_ = null;
      }
      return serverInfoBuilder_;
    }

    private int status_ ;
    /**
     * <pre>
     *0成功
     * </pre>
     *
     * <code>int32 status = 2;</code>
     * @return The status.
     */
    public int getStatus() {
      return status_;
    }
    /**
     * <pre>
     *0成功
     * </pre>
     *
     * <code>int32 status = 2;</code>
     * @param value The status to set.
     * @return This builder for chaining.
     */
    public Builder setStatus(int value) {
      
      status_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *0成功
     * </pre>
     *
     * <code>int32 status = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearStatus() {
      
      status_ = 0;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:ProtoMessage.ServerRegisterUpdateResponse)
  }

  // @@protoc_insertion_point(class_scope:ProtoMessage.ServerRegisterUpdateResponse)
  private static final org.mmo.message.ServerRegisterUpdateResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.mmo.message.ServerRegisterUpdateResponse();
  }

  public static org.mmo.message.ServerRegisterUpdateResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ServerRegisterUpdateResponse>
      PARSER = new com.google.protobuf.AbstractParser<ServerRegisterUpdateResponse>() {
    @Override
    public ServerRegisterUpdateResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ServerRegisterUpdateResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ServerRegisterUpdateResponse> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<ServerRegisterUpdateResponse> getParserForType() {
    return PARSER;
  }

  @Override
  public org.mmo.message.ServerRegisterUpdateResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

