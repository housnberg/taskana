package pro.taskana.rest;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.ldap.LdapClient;
import pro.taskana.ldap.LdapConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Example Application showing the implementation of taskana-rest-spring.
 */
@SpringBootApplication
@EnableScheduling
@Import({SampleConfiguration.class, LdapConfiguration.class, RestConfiguration.class})
public class ExampleRestApplication {

    @Autowired
    private LdapClient ldapClient;

    public static void main(String[] args) {
        SpringApplication.run(ExampleRestApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        props.setUrl("jdbc:db2://172.16.160.101:50000/tskdb");
        return props;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @DependsOn("taskanaEngineConfiguration") // generate sample data after schema was inserted
    public SampleDataGenerator generateSampleData(DataSource dataSource) throws SQLException {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource);
        sampleDataGenerator.generateSampleData();
        return sampleDataGenerator;
    }

    @PostConstruct
    private void init() {
        if (!ldapClient.useLdap()) {
            AccessIdController.setLdapCache(new LdapCacheTestImpl());
        }
    }
}
