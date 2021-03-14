package com.fo0.vaadin.scrumtool.ui;

import java.security.Security;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import de.dentrassi.crypto.pem.PemKeyStoreProvider;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Main extends SpringBootServletInitializer {

	public static void main(String[] args) {
		Security.addProvider(new PemKeyStoreProvider());
		//@formatter:off
		new SpringApplicationBuilder(Main.class)
			.properties("vaadin.heartbeatinterval=5")
			.profiles(Profiles.H2_DRIVER)
			.run(args);
		//@formatter:on
	}

}
