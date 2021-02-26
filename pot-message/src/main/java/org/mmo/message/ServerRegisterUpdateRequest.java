// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ServerMessage.proto

package org.mmo.message;

/**
 * <pre>
 * 注册更新服务器
 * </pre>
 *
 * Protobuf type {@code ProtoMessage.ServerRegisterUpdateRequest}
 */
public  final class ServerRegisterUpdateRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ProtoMessage.ServerRegisterUpdateRequest)
    ServerRegisterUpdateRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ServerRegisterUpdateRequest.newBuilder() to construct.
  private ServerRegisterUpdateRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ServerRegisterUpdateRequest() {
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new ServerRegisterUpdateRequest();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ServerRegisterUpdateRequest(
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
          case 18: {
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
    return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateRequest_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.mmo.message.ServerRegisterUpdateRequest.class, org.mmo.message.ServerRegisterUpdateRequest.Builder.class);
  }

  public static final int SERVERINFO_FIELD_NUMBER = 2;
  private org.mmo.message.ServerInfo serverInfo_;
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
   * @return Whether the serverInfo field is set.
   */
  public boolean hasServerInfo() {
    return serverInfo_ != null;
  }
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
   * @return The serverInfo.
   */
  public org.mmo.message.ServerInfo getServerInfo() {
    return serverInfo_ == null ? org.mmo.message.ServerInfo.getDefaultInstance() : serverInfo_;
  }
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
   */
  public org.mmo.message.ServerInfoOrBuilder getServerInfoOrBuilder() {
    return getServerInfo();
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
      output.writeMessage(2, getServerInfo());
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
        .computeMessageSize(2, getServerInfo());
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
    if (!(obj instanceof org.mmo.message.ServerRegisterUpdateRequest)) {
      return super.equals(obj);
    }
    org.mmo.message.ServerRegisterUpdateRequest other = (org.mmo.message.ServerRegisterUpdateRequest) obj;

    if (hasServerInfo() != other.hasServerInfo()) return false;
    if (hasServerInfo()) {
      if (!getServerInfo()
          .equals(other.getServerInfo())) return false;
    }
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
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.mmo.message.ServerRegisterUpdateRequest parseFrom(
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
  public static Builder newBuilder(org.mmo.message.ServerRegisterUpdateRequest prototype) {
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
   * Protobuf type {@code ProtoMessage.ServerRegisterUpdateRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ProtoMessage.ServerRegisterUpdateRequest)
      org.mmo.message.ServerRegisterUpdateRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateRequest_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.mmo.message.ServerRegisterUpdateRequest.class, org.mmo.message.ServerRegisterUpdateRequest.Builder.class);
    }

    // Construct using org.mmo.message.ServerRegisterUpdateRequest.newBuilder()
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
      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.mmo.message.ServerMessage.internal_static_ProtoMessage_ServerRegisterUpdateRequest_descriptor;
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateRequest getDefaultInstanceForType() {
      return getDefaultInstance();
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateRequest build() {
      org.mmo.message.ServerRegisterUpdateRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public org.mmo.message.ServerRegisterUpdateRequest buildPartial() {
      org.mmo.message.ServerRegisterUpdateRequest result = new org.mmo.message.ServerRegisterUpdateRequest(this);
      if (serverInfoBuilder_ == null) {
        result.serverInfo_ = serverInfo_;
      } else {
        result.serverInfo_ = serverInfoBuilder_.build();
      }
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
      if (other instanceof org.mmo.message.ServerRegisterUpdateRequest) {
        return mergeFrom((org.mmo.message.ServerRegisterUpdateRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.mmo.message.ServerRegisterUpdateRequest other) {
      if (other == getDefaultInstance()) return this;
      if (other.hasServerInfo()) {
        mergeServerInfo(other.getServerInfo());
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
      org.mmo.message.ServerRegisterUpdateRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.mmo.message.ServerRegisterUpdateRequest) e.getUnfinishedMessage();
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
     * @return Whether the serverInfo field is set.
     */
    public boolean hasServerInfo() {
      return serverInfoBuilder_ != null || serverInfo_ != null;
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
     */
    public org.mmo.message.ServerInfo.Builder getServerInfoBuilder() {
      
      onChanged();
      return getServerInfoFieldBuilder().getBuilder();
    }
    /**
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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
     * <code>.ProtoMessage.ServerInfo serverInfo = 2;</code>
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


    // @@protoc_insertion_point(builder_scope:ProtoMessage.ServerRegisterUpdateRequest)
  }

  // @@protoc_insertion_point(class_scope:ProtoMessage.ServerRegisterUpdateRequest)
  private static final org.mmo.message.ServerRegisterUpdateRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.mmo.message.ServerRegisterUpdateRequest();
  }

  public static org.mmo.message.ServerRegisterUpdateRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ServerRegisterUpdateRequest>
      PARSER = new com.google.protobuf.AbstractParser<ServerRegisterUpdateRequest>() {
    @Override
    public ServerRegisterUpdateRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ServerRegisterUpdateRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ServerRegisterUpdateRequest> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<ServerRegisterUpdateRequest> getParserForType() {
    return PARSER;
  }

  @Override
  public org.mmo.message.ServerRegisterUpdateRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

