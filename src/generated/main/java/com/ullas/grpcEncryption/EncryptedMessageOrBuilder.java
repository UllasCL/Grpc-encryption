// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message.proto

package com.ullas.grpcEncryption;

public interface EncryptedMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:message.EncryptedMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bytes data = 1;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>string key = 2;</code>
   * @return The key.
   */
  java.lang.String getKey();
  /**
   * <code>string key = 2;</code>
   * @return The bytes for key.
   */
  com.google.protobuf.ByteString
      getKeyBytes();
}
