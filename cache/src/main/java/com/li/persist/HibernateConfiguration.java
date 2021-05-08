package com.li.persist;

import com.alibaba.druid.pool.DruidDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

/**
 * @author li-yuanwen
 * Hibernate 配置,移至需要使用数据库的服务器上
 */
@Configuration
public class HibernateConfiguration {

    /** jdbc 地址 **/
    @Value("${jdbc.url}")
    private String url;

    /** jdbc用户账号 **/
    @Value("${jdbc.username}")
    private String userName;

    /** jdbc账号密码 **/
    @Value("${jdbc.password}")
    private String password;

    /** druid连接池配置 **/
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }

    /** sessionFactory **/
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
