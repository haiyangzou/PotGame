// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PlayerMessage.proto

package org.mmo.message;

public interface PlayerInfoResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProtoMessage.PlayerInfoResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *用户id
   * </pre>
   *
   * <code>int64 userId = 1;</code>
   * @return The userId.
   */
  long getUserId();

  /**
   * <pre>
   *玩家信息
   * </pre>
   *
   * <code>.ProtoMessage.PlayerInfo player = 2;</code>
   * @return Whether the player field is set.
   */
  boolean hasPlayer();
  /**
   * <pre>
   *玩家信息
   * </pre>
   *
   * <code>.ProtoMessage.PlayerInfo player = 2;</code>
   * @return The player.
   */
  org.mmo.message.PlayerInfo getPlayer();
  /**
   * <pre>
   *玩家信息
   * </pre>
   *
   * <code>.ProtoMessage.PlayerInfo player = 2;</code>
   */
  org.mmo.message.PlayerInfoOrBuilder getPlayerOrBuilder();
}
