package com.example.docu.data;

public class AuthenticationData {
  private final String accessToken;

  private final String accountId;

  public AuthenticationData(String accessToken, String accountId) {
    this.accessToken = accessToken;
    this.accountId = accountId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getAccountId() {
    return accountId;
  }

}
