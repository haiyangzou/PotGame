// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ItemMessage.proto

package org.mmo.message;

public interface ItemInfoOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProtoMessage.ItemInfo)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *id
   * </pre>
   *
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <pre>
   *配置id
   * </pre>
   *
   * <code>int32 configId = 2;</code>
   * @return The configId.
   */
  int getConfigId();

  /**
   * <pre>
   *数量
   * </pre>
   *
   * <code>int64 count = 3;</code>
   * @return The count.
   */
  long getCount();
}
