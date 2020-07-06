package com.fo0.vaadin.scrumtool.test.data;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.fo0.vaadin.scrumtool.ui.data.repository")
@Profile(Profiles.MYSQL_DRIVER)
public class MySqlDatabaseConfig {

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.url(String.format(
						"jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true",
						"localhost", "3306", "testdb3"))
				.driverClassName("com.mysql.cj.jdbc.Driver").username("root").password("max123").build();
	}

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
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}

	private final Properties additionalProperties() {
		final Properties p = new Properties();
		p.setProperty("hibernate.hbm2ddl.auto", "create-drop"); // update / create-drop
		// MYSQL 5
//		p.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		p.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

		// Whether to enable logging of SQL statements "true" | "false" -> currently not
		// working
		p.put("spring.jpa.show_sql", "false");

		// make objects detached
		p.put("spring.jpa.open-in-view", "false");

		// database metrics: "true" | "false"
		p.put("hibernate.generate_statistics", "false");
		return p;
	}

}
