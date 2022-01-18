package com.fo0.vaadin.scrumtool.ui;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;
import de.dentrassi.crypto.pem.PemKeyStoreProvider;
import java.security.Security;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Main extends SpringBootServletInitializer {

  public static void main(String[] args) {
    Security.addProvider(new PemKeyStoreProvider());
    new SpringApplicationBuilder(Main.class)
        .properties("vaadin.heartbeatinterval=5")
        .properties("spring.main.allow-circular-references=true")
        .profiles(Profiles.H2_DRIVER)
        .run(args);
  }

}
