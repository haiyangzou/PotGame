// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: AccountMessage.proto

package org.mmo.message;

public final class AccountMessage {
  private AccountMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoMessage_LoginRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoMessage_LoginRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoMessage_LoginResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoMessage_LoginResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoMessage_HeartRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoMessage_HeartRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoMessage_HeartResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoMessage_HeartResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024AccountMessage.proto\022\014ProtoMessage\"1\n\014" +
      "LoginRequest\022\017\n\007account\030\001 \001(\t\022\020\n\010passwor" +
      "d\030\002 \001(\t\"\037\n\rLoginResponse\022\016\n\006userId\030\001 \001(\003" +
      "\"\016\n\014HeartRequest\"\035\n\rHeartResponse\022\014\n\004tim" +
      "e\030\001 \001(\0032R\n\016AccountService\022@\n\005login\022\032.Pro" +
      "toMessage.LoginRequest\032\033.ProtoMessage.Lo" +
      "ginResponseB,\n\017org.mmo.messageB\016AccountM" +
      "essageP\001Z\007messageb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_ProtoMessage_LoginRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ProtoMessage_LoginRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoMessage_LoginRequest_descriptor,
        new java.lang.String[] { "Account", "Password", });
    internal_static_ProtoMessage_LoginResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ProtoMessage_LoginResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoMessage_LoginResponse_descriptor,
        new java.lang.String[] { "UserId", });
    internal_static_ProtoMessage_HeartRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_ProtoMessage_HeartRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoMessage_HeartRequest_descriptor,
        new java.lang.String[] { });
    internal_static_ProtoMessage_HeartResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_ProtoMessage_HeartResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoMessage_HeartResponse_descriptor,
        new java.lang.String[] { "Time", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}