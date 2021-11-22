package com.weird.config.data;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by summer on 2016/11/25.
 *
 * @author summer
 * @date 2016/11/25
 */
@Configuration
@MapperScan(basePackages = "com.weird.mapper.record", sqlSessionTemplateRef = "recordSqlSessionTemplate")
public class RecordDataConfig {
    @Value("${mapperLocate.record}")
    String mapperLocate;

    @Bean(name = "recordDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.record")
    public DataSource recordDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "recordSqlSessionFactory")
    public SqlSessionFactory recordSqlSessionFactory(@Qualifier("recordDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocate));
        return bean.getObject();
    }

    @Bean(name = "recordTransactionManager")
    public DataSourceTransactionManager recordTransactionManager(@Qualifier("recordDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "recordSqlSessionTemplate")
    public SqlSessionTemplate recordSqlSessionTemplate(@Qualifier("recordSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
