package de.bund.bva.isyfact.persistence.autoconfigure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import de.bund.bva.isyfact.persistence.autoconfigure.properties.DatabaseProperties;
import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

/**
 * Spring configuration of the persistence layer for Oracle.
 */
@Configuration
@ConditionalOnProperty(name = "isy.persistence.datasource.url")
@EnableConfigurationProperties
@Import(DatabaseInitializationDependencyConfigurer.class)
public class IsyPersistenceOracleAutoConfiguration {

    /**
     * Creates a bean for the Health Check of the data source.
     *
     * @param dataSource
     *         the initialized app data source
     * @return Bean oracleDataSourceHealthIndicator
     */
    @Bean
    @ConditionalOnMissingBean(DataSourceHealthIndicator.class)
    public DataSourceHealthIndicator oracleDataSourceHealthIndicator(@Qualifier("appDataSource") DataSource dataSource) {
        DataSourceHealthIndicator dataSourceHealthIndicator = new DataSourceHealthIndicator();
        dataSourceHealthIndicator.setDataSource(dataSource);
        dataSourceHealthIndicator.setQuery("select BANNER from V$VERSION where BANNER like 'Oracle%'");

        return dataSourceHealthIndicator;
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "isy.persistence.datasource")
    public DatabaseProperties databaseProperties() {
        return new DatabaseProperties();
    }

    @Bean("dataSource")
    @ConfigurationProperties(prefix = "isy.persistence.datasource.config")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    @DependsOnDatabaseInitialization
    public IsyDataSource appDataSource(@Qualifier("dataSource") DataSource dataSource,
                                       DatabaseProperties databaseProperties) {
        IsyDataSource isyDataSource = new IsyDataSource();
        isyDataSource.setSchemaVersion(databaseProperties.getSchemaVersion());
        isyDataSource.setInvalidSchemaVersionAction(databaseProperties.getSchemaInvalidVersionAction());
        isyDataSource.setTargetDataSource(dataSource);
        return isyDataSource;
    }

}
