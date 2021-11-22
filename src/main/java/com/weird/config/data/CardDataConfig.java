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
@MapperScan(basePackages = "com.weird.mapper.card", sqlSessionTemplateRef = "cardSqlSessionTemplate")
public class CardDataConfig {
    @Value("${mapperLocate.card}")
    String mapperLocate;

    @Bean(name = "cardDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.card")
    public DataSource cardDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cardSqlSessionFactory")
    public SqlSessionFactory cardSqlSessionFactory(@Qualifier("cardDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocate));
        return bean.getObject();
    }

    @Bean(name = "cardTransactionManager")
    public DataSourceTransactionManager cardTransactionManager(@Qualifier("cardDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "cardSqlSessionTemplate")
    public SqlSessionTemplate cardSqlSessionTemplate(@Qualifier("cardSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
