package com.fo0.vaadin.scrumtool.ui.data.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.fo0.vaadin.scrumtool.ui.data.repository")
@Profile(Profiles.H2_DRIVER)
public class PersistenceConfig {

	@Value("${scrumtool.database.inmem:false}")
	private boolean databaseInMemory;
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("com.fo0.vaadin.scrumtool.ui.data.table");

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		if(databaseInMemory) {
			dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"); // in memory: jdbc:h2:mem:db;DB_CLOSE_DELAY=-1			
		} else {
			dataSource.setUrl("jdbc:h2:file:./database"); // in memory: jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
		}
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");

		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	final Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.ddl-auto", "update"); // dev: create-drop || validate
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update"); // according to hibernate.ddl-auto
		hibernateProperties.setProperty("spring.jpa.generate-ddl", "true");
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProperties.setProperty("hibernate.show_sql", "false");

		return hibernateProperties;
	}


}
