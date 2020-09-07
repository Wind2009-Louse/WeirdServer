package com.weird.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Spring的配置类
 *
 * @author Nidhogg
 */
@Configuration
@ComponentScan(value = "com.weird", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {org.springframework.stereotype.Controller.class})
})
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class})
@MapperScan(basePackages = "com.weird.mapper")
public class RootConfig {

    /**
     * 配置MyBatis的Bean工厂
     *
     * @param dataSource
     * @return
     * @throws IOException
     */
    @Bean("sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:/mapper/*.xml"));

        sqlSessionFactoryBean.setTypeAliasesPackage("com.weird.model");

        return sqlSessionFactoryBean;
    }
}
