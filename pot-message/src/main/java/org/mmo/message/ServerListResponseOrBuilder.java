// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ServerMessage.proto

package org.mmo.message;

public interface ServerListResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProtoMessage.ServerListResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 服务器列表
   * </pre>
   *
   * <code>repeated .ProtoMessage.ServerInfo server = 2;</code>
   */
  java.util.List<org.mmo.message.ServerInfo> 
      getServerList();
  /**
   * <pre>
   * 服务器列表
   * </pre>
   *
   * <code>repeated .ProtoMessage.ServerInfo server = 2;</code>
   */
  org.mmo.message.ServerInfo getServer(int index);
  /**
   * <pre>
   * 服务器列表
   * </pre>
   *
   * <code>repeated .ProtoMessage.ServerInfo server = 2;</code>
   */
  int getServerCount();
  /**
   * <pre>
   * 服务器列表
   * </pre>
   *
   * <code>repeated .ProtoMessage.ServerInfo server = 2;</code>
   */
  java.util.List<? extends org.mmo.message.ServerInfoOrBuilder> 
      getServerOrBuilderList();
  /**
   * <pre>
   * 服务器列表
   * </pre>
   *
   * <code>repeated .ProtoMessage.ServerInfo server = 2;</code>
   */
  org.mmo.message.ServerInfoOrBuilder getServerOrBuilder(
      int index);
}