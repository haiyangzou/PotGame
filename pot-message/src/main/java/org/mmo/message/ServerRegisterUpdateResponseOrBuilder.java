// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ServerMessage.proto

package org.mmo.message;

public interface ServerRegisterUpdateResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProtoMessage.ServerRegisterUpdateResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   * @return Whether the serverInfo field is set.
   */
  boolean hasServerInfo();
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   * @return The serverInfo.
   */
  org.mmo.message.ServerInfo getServerInfo();
  /**
   * <code>.ProtoMessage.ServerInfo serverInfo = 1;</code>
   */
  org.mmo.message.ServerInfoOrBuilder getServerInfoOrBuilder();

  /**
   * <pre>
   *0成功
   * </pre>
   *
   * <code>int32 status = 2;</code>
   * @return The status.
   */
  int getStatus();
}
