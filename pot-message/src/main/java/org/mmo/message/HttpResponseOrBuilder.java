// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ServerMessage.proto

package org.mmo.message;

public interface HttpResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProtoMessage.HttpResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *结果码 200成功
   * </pre>
   *
   * <code>int32 code = 2;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <pre>
   *返回结果字符串
   * </pre>
   *
   * <code>string result = 3;</code>
   * @return The result.
   */
  java.lang.String getResult();
  /**
   * <pre>
   *返回结果字符串
   * </pre>
   *
   * <code>string result = 3;</code>
   * @return The bytes for result.
   */
  com.google.protobuf.ByteString
      getResultBytes();
}
