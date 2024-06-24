package com.example.docu;

import com.example.docu.controller.FocusedViewController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocuApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocuApplication.class, args);

    /*
    FocusedViewController controller = new FocusedViewController();

    try {
      controller.getAccess();
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }

     */

  }


}
