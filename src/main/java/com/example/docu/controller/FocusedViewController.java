package com.example.docu.controller;


import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;

import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.example.docu.data.AuthenticationData;
import com.example.docu.data.EmbeddedSigningResponse;
import com.example.docu.services.AuthenticationService;
import com.example.docu.services.FocusedViewService;
import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


/**
 * Used to generate an envelope and allow user to sign it directly from the app without having to open an email.
 */
@RestController
@RequestMapping("/embedding")
public class FocusedViewController {
  public static final String EMBED = "pages/esignature/examples/embed";

  public static final String INTEGRATION_KEY = "integrationKey";

  public static final String URL = "url";

  public static final String ENVELOPE_ID = "envelopeId";

  public static final String DOCUMENTATION = "documentation";

  private static final String BEARER_AUTHENTICATION = "Bearer ";

  @Autowired
  private Environment env;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private FocusedViewService focusedViewService;

  @Value("${docusign.user.email}")
  private String userEmail;

  @Value("${docusign.user.name}")
  private String userName;

  static String DevCenterPage = "https://developers.docusign.com/platform/auth/consent";


 // private String email;

  //private String username;

  private Properties configProperties;

  public FocusedViewController() {
    /*
    Scanner scanner = new Scanner(System. in);
    System.out.print("Enter the signer's email address: \n");
    //String signerEmail = "kelvinjust4school@gmail.com";
    String signerEmail = scanner. nextLine();
    this.email = signerEmail;
    System.out.print("Enter the signer's name: \n");
    //String signerName = "kx";
    String signerName = scanner. nextLine();
    this.username = signerName;

     */
  }

  @CrossOrigin(origins = "http://localhost:4200")
  @GetMapping("/getToken")
  public AuthenticationData getToken() throws IOException {
    Properties prop = new Properties();
    String configFileName = env.getProperty("docusign.config.path");
    FileInputStream fis = new FileInputStream(configFileName);
    prop.load(fis);
    try {
      Scanner scanner = new Scanner(System.in);
      System.out.print("Enter the signer's email address: \n");
      String signerEmail = "kelvinjust4school@gmail.com";//scanner. nextLine();
      System.out.print("Enter the signer's name: \n");
      String signerName = "kx"; //scanner. nextLine();

      // Get access token and accountId
      ApiClient apiClient = authenticationService.authenticateWithJWTClient();
      OAuthToken oAuthToken = authenticationService.getAuthTokenFocusedView(apiClient, prop, focusedViewService.privateKey());

      String accessToken = oAuthToken.getAccessToken();
      UserInfo userInfo = apiClient.getUserInfo(accessToken);
      String accountId = userInfo.getAccounts().get(0).getAccountId();
      return new AuthenticationData(accessToken, accountId);

    } catch (ApiException exp) {
      if (exp.getMessage().contains("consent_required")) {
        try {
          System.out.println(
              "Consent required, please provide consent in browser window and then run this app again.");
          Desktop.getDesktop().browse(new URI(
              "https://account-d.docusign.com/oauth/auth?response_type=code&scope=impersonation%20signature&client_id="
                  + prop.getProperty("clientId") + "&redirect_uri=" + DevCenterPage));
        } catch (Exception e) {
          System.out.print("Error!!!  ");
          System.out.print(e.getMessage());
        }

      }

      System.out.println(exp.getMessage());
    } catch (Exception e) {
      System.out.print("Error!!!  ");
      System.out.print(e.getMessage());
    }

    return null;
  }

  @CrossOrigin(origins = "http://localhost:4200")
  @GetMapping("/access")
  public EmbeddedSigningResponse getAccess() throws IOException {
    try
    {
      Properties prop = focusedViewService.properties();
      this.configProperties = prop;
      // Get access token and accountId
      ApiClient apiClient = authenticationService.authenticateWithJWTClient();
      OAuthToken oAuthToken = authenticationService.getAuthTokenFocusedView(apiClient, prop, focusedViewService.privateKey());
      String accessToken = oAuthToken.getAccessToken();
      UserInfo userInfo = apiClient.getUserInfo(accessToken);
      String accountId = userInfo.getAccounts().get(0).getAccountId();
      apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);

      String[] envelopeIdAndRedirectUrl = focusedViewService.sendEnvelopeWithFocusedView(
          userEmail,
          userName,
          apiClient,
          accountId,
          "localhost:8080/ds-return");

      System.out.println(envelopeIdAndRedirectUrl.length);
      for (int i = 0; i < envelopeIdAndRedirectUrl.length; i++)
      {
        System.out.println(envelopeIdAndRedirectUrl[i]);
      }

      EmbeddedSigningResponse embeddedSigningResponse = new EmbeddedSigningResponse(envelopeIdAndRedirectUrl[0], envelopeIdAndRedirectUrl[1]);
      return embeddedSigningResponse;
    }
    catch (ApiException exp)
    {
      if (exp.getMessage().contains("consent_required"))
      {
        try
        {
          System.out.println ("Consent required, please provide consent in browser window and then run this app again.");
          Desktop.getDesktop().browse(new URI("https://account-d.docusign.com/oauth/auth?response_type=code&scope=impersonation%20signature&client_id=" + configProperties.getProperty("clientId") + "&redirect_uri=" + DevCenterPage));
        }
        catch (Exception e)
        {
          System.out.print ("Error!!!  ");
          System.out.print (e.getMessage());
        }

      }

      System.out.println(exp.getMessage());
    }
    catch (Exception e)
    {
      System.out.print ("Error!!!  ");
      System.out.print (e.getMessage());
    }
    return null;
  }
}
