// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: hello_world.proto

package com.ullas.grpcEncryption;

public final class HelloWorldProto {
  private HelloWorldProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_helloworld_HelloRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_helloworld_HelloRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_helloworld_HelloResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_helloworld_HelloResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_helloworld_EncryptedMessageReqRes_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_helloworld_EncryptedMessageReqRes_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021hello_world.proto\022\nhelloworld\"\034\n\014Hello" +
      "Request\022\014\n\004name\030\001 \001(\t\" \n\rHelloResponse\022\017" +
      "\n\007message\030\001 \001(\t\"?\n\026EncryptedMessageReqRe" +
      "s\022\017\n\007payload\030\001 \001(\014\022\024\n\014encRandomKey\030\002 \001(\t" +
      "2\253\001\n\007Greeter\022A\n\010SayHello\022\030.helloworld.He" +
      "lloRequest\032\031.helloworld.HelloResponse\"\000\022" +
      "]\n\021SayEncryptedHello\022\".helloworld.Encryp" +
      "tedMessageReqRes\032\".helloworld.EncryptedM" +
      "essageReqRes\"\000B-\n\030com.ullas.grpcEncrypti" +
      "onB\017HelloWorldProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_helloworld_HelloRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_helloworld_HelloRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_helloworld_HelloRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_helloworld_HelloResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_helloworld_HelloResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_helloworld_HelloResponse_descriptor,
        new java.lang.String[] { "Message", });
    internal_static_helloworld_EncryptedMessageReqRes_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_helloworld_EncryptedMessageReqRes_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_helloworld_EncryptedMessageReqRes_descriptor,
        new java.lang.String[] { "Payload", "EncRandomKey", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
