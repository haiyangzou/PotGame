// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server.proto

package org.pot.message.protocol;

/**
 * Protobuf type {@code ProtoMessage.ServerNetPong}
 */
public  final class ServerNetPong extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ProtoMessage.ServerNetPong)
    ServerNetPongOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ServerNetPong.newBuilder() to construct.
  private ServerNetPong(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ServerNetPong() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ServerNetPong();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ServerNetPong(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
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
            org.pot.message.protocol.ServerNetPing.Builder subBuilder = null;
            if (ping_ != null) {
              subBuilder = ping_.toBuilder();
            }
            ping_ = input.readMessage(org.pot.message.protocol.ServerNetPing.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(ping_);
              ping_ = subBuilder.buildPartial();
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
    return org.pot.message.protocol.ServerMessage.internal_static_ProtoMessage_ServerNetPong_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.pot.message.protocol.ServerMessage.internal_static_ProtoMessage_ServerNetPong_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.pot.message.protocol.ServerNetPong.class, org.pot.message.protocol.ServerNetPong.Builder.class);
  }

  public static final int PING_FIELD_NUMBER = 1;
  private org.pot.message.protocol.ServerNetPing ping_;
  /**
   * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
   * @return Whether the ping field is set.
   */
  public boolean hasPing() {
    return ping_ != null;
  }
  /**
   * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
   * @return The ping.
   */
  public org.pot.message.protocol.ServerNetPing getPing() {
    return ping_ == null ? org.pot.message.protocol.ServerNetPing.getDefaultInstance() : ping_;
  }
  /**
   * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
   */
  public org.pot.message.protocol.ServerNetPingOrBuilder getPingOrBuilder() {
    return getPing();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (ping_ != null) {
      output.writeMessage(1, getPing());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (ping_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getPing());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.pot.message.protocol.ServerNetPong)) {
      return super.equals(obj);
    }
    org.pot.message.protocol.ServerNetPong other = (org.pot.message.protocol.ServerNetPong) obj;

    if (hasPing() != other.hasPing()) return false;
    if (hasPing()) {
      if (!getPing()
          .equals(other.getPing())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasPing()) {
      hash = (37 * hash) + PING_FIELD_NUMBER;
      hash = (53 * hash) + getPing().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.pot.message.protocol.ServerNetPong parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.pot.message.protocol.ServerNetPong parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.pot.message.protocol.ServerNetPong parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.pot.message.protocol.ServerNetPong parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.pot.message.protocol.ServerNetPong prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code ProtoMessage.ServerNetPong}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ProtoMessage.ServerNetPong)
      org.pot.message.protocol.ServerNetPongOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.pot.message.protocol.ServerMessage.internal_static_ProtoMessage_ServerNetPong_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.pot.message.protocol.ServerMessage.internal_static_ProtoMessage_ServerNetPong_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.pot.message.protocol.ServerNetPong.class, org.pot.message.protocol.ServerNetPong.Builder.class);
    }

    // Construct using org.pot.message.protocol.ServerNetPong.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (pingBuilder_ == null) {
        ping_ = null;
      } else {
        ping_ = null;
        pingBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.pot.message.protocol.ServerMessage.internal_static_ProtoMessage_ServerNetPong_descriptor;
    }

    @java.lang.Override
    public org.pot.message.protocol.ServerNetPong getDefaultInstanceForType() {
      return org.pot.message.protocol.ServerNetPong.getDefaultInstance();
    }

    @java.lang.Override
    public org.pot.message.protocol.ServerNetPong build() {
      org.pot.message.protocol.ServerNetPong result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.pot.message.protocol.ServerNetPong buildPartial() {
      org.pot.message.protocol.ServerNetPong result = new org.pot.message.protocol.ServerNetPong(this);
      if (pingBuilder_ == null) {
        result.ping_ = ping_;
      } else {
        result.ping_ = pingBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.pot.message.protocol.ServerNetPong) {
        return mergeFrom((org.pot.message.protocol.ServerNetPong)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.pot.message.protocol.ServerNetPong other) {
      if (other == org.pot.message.protocol.ServerNetPong.getDefaultInstance()) return this;
      if (other.hasPing()) {
        mergePing(other.getPing());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.pot.message.protocol.ServerNetPong parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.pot.message.protocol.ServerNetPong) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private org.pot.message.protocol.ServerNetPing ping_;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.pot.message.protocol.ServerNetPing, org.pot.message.protocol.ServerNetPing.Builder, org.pot.message.protocol.ServerNetPingOrBuilder> pingBuilder_;
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     * @return Whether the ping field is set.
     */
    public boolean hasPing() {
      return pingBuilder_ != null || ping_ != null;
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     * @return The ping.
     */
    public org.pot.message.protocol.ServerNetPing getPing() {
      if (pingBuilder_ == null) {
        return ping_ == null ? org.pot.message.protocol.ServerNetPing.getDefaultInstance() : ping_;
      } else {
        return pingBuilder_.getMessage();
      }
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public Builder setPing(org.pot.message.protocol.ServerNetPing value) {
      if (pingBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ping_ = value;
        onChanged();
      } else {
        pingBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public Builder setPing(
        org.pot.message.protocol.ServerNetPing.Builder builderForValue) {
      if (pingBuilder_ == null) {
        ping_ = builderForValue.build();
        onChanged();
      } else {
        pingBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public Builder mergePing(org.pot.message.protocol.ServerNetPing value) {
      if (pingBuilder_ == null) {
        if (ping_ != null) {
          ping_ =
            org.pot.message.protocol.ServerNetPing.newBuilder(ping_).mergeFrom(value).buildPartial();
        } else {
          ping_ = value;
        }
        onChanged();
      } else {
        pingBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public Builder clearPing() {
      if (pingBuilder_ == null) {
        ping_ = null;
        onChanged();
      } else {
        ping_ = null;
        pingBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public org.pot.message.protocol.ServerNetPing.Builder getPingBuilder() {
      
      onChanged();
      return getPingFieldBuilder().getBuilder();
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    public org.pot.message.protocol.ServerNetPingOrBuilder getPingOrBuilder() {
      if (pingBuilder_ != null) {
        return pingBuilder_.getMessageOrBuilder();
      } else {
        return ping_ == null ?
            org.pot.message.protocol.ServerNetPing.getDefaultInstance() : ping_;
      }
    }
    /**
     * <code>.ProtoMessage.ServerNetPing ping = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.pot.message.protocol.ServerNetPing, org.pot.message.protocol.ServerNetPing.Builder, org.pot.message.protocol.ServerNetPingOrBuilder> 
        getPingFieldBuilder() {
      if (pingBuilder_ == null) {
        pingBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.pot.message.protocol.ServerNetPing, org.pot.message.protocol.ServerNetPing.Builder, org.pot.message.protocol.ServerNetPingOrBuilder>(
                getPing(),
                getParentForChildren(),
                isClean());
        ping_ = null;
      }
      return pingBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:ProtoMessage.ServerNetPong)
  }

  // @@protoc_insertion_point(class_scope:ProtoMessage.ServerNetPong)
  private static final org.pot.message.protocol.ServerNetPong DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.pot.message.protocol.ServerNetPong();
  }

  public static org.pot.message.protocol.ServerNetPong getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ServerNetPong>
      PARSER = new com.google.protobuf.AbstractParser<ServerNetPong>() {
    @java.lang.Override
    public ServerNetPong parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ServerNetPong(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ServerNetPong> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ServerNetPong> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.pot.message.protocol.ServerNetPong getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

