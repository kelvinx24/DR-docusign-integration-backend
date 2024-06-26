package com.example.docu.services;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

  public AuthenticationService() {
  }

  public static ApiClient authenticateWithJWTClient() {
    ApiClient apiClient = new com.docusign.esign.client.ApiClient("https://demo.docusign.net/restapi");
    apiClient.setOAuthBasePath("account-d.docusign.com");
    return apiClient;
  }

  public static OAuthToken getOAuthToken(ApiClient apiClient, Properties prop, List<String> scopes, byte[] pk)
      throws ApiException, IOException {
    OAuthToken oAuthToken = apiClient.requestJWTUserToken(
        prop.getProperty("clientId"),
        prop.getProperty("userId"),
        scopes,
        pk,
        3600);

    return oAuthToken;
  }

  public static List<String> getFocusedViewScopes() {
    List<String> scopes = new ArrayList<String>();
    scopes.add("signature");
    scopes.add("impersonation");
    return scopes;
  }

  public static OAuthToken getAuthTokenFocusedView(ApiClient apiClient, Properties prop, byte[] pk) throws IOException, ApiException {
    return getOAuthToken(apiClient, prop, getFocusedViewScopes(), pk);
  }

  public static byte[] loadPrivateKey(String privateKeyFile) {
    try {
      byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyFile));
      return privateKeyBytes;
    } catch (Exception ex) {
     AuthenticationService.log.debug("Error loading private key: " + ex.getMessage());
    }

    return null;

  }

  public static Properties loadConfigFile(String configFilePath)  {
    try {
      Properties prop = new Properties();
      FileInputStream fis = new FileInputStream(configFilePath);
      prop.load(fis);
      return prop;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }
}
