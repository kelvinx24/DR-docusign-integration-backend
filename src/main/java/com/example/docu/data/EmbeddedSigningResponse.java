package com.example.docu.data;

public class EmbeddedSigningResponse {
  private final String token;

  private final String documentURL;

  public EmbeddedSigningResponse(String token, String documentURL) {
    this.token = token;
    this.documentURL = documentURL;
  }

  public String getToken() {
    return token;
  }

  public String getDocumentURL() {
    return documentURL;
  }
}
