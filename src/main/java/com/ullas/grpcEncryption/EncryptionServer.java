package com.ullas.grpcEncryption;


import java.io.UnsupportedEncodingException;

/**
 * The type Encryption server.
 */
public class EncryptionServer {
  public static void main(String[] args) {
    String testString = "Crunchify Example on Byte[] to String";

    byte[] bytesData = testString.getBytes();

    System.out.println("testString : " + testString);
    System.out.println(
        "\nbytesData : " + bytesData);  // .getBytes on String will return Hashcode value
    System.out.println("bytesData.toString() : " + bytesData
        .toString().getBytes());  // .toString() will return Hashcode value

    String decodedData = new String(bytesData);  // Create new String Object and assign byte[] to it
    System.out.println("\nText Decryted : " + decodedData);
    String decodedDataUsingUTF8;
    try {
      decodedDataUsingUTF8 = new String(bytesData, "UTF-8");  // Best way to decode using "UTF-8"
      System.out.println("Text Decryted using UTF-8 : " + decodedDataUsingUTF8);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
