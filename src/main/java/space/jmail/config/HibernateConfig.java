package space.jmail.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import space.jmail.dao.*;
import space.jmail.service.*;
import space.jmail.util.Helper;


import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by kernel32 on 10.05.2017.
 */
@Configuration
@ComponentScan("space.jmail")
@EnableTransactionManagement
public class HibernateConfig {

    @Autowired
    StatisticDao statisticDao;
    @Autowired
    SenderDao senderDao;
    @Autowired
    RealReceiverDao realReceiverDao;
    @Autowired
    FakeReceiverDao fakeReceiverDao;
    @Autowired
    MessageDao messageDao;

    @Bean
    public RealReceiverService receiverService(){
        return new RealReceiverService(realReceiverDao);
    }

    @Bean
    public SenderService senderService(){
        return new SenderService(senderDao);
    }

    @Bean
    public StatService statService() {
        return new StatService(statisticDao);
    }

    @Bean
    public FakeReceiverService fakeReceiverService(){
        return new FakeReceiverService(fakeReceiverDao);
    }

    @Bean
    public MessageService messageService(){
        return new MessageService(messageDao);
    }

    @Bean
    public Helper helper(){
        return new Helper();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("space.jmail.entity");
        sessionFactory.setHibernateProperties(getHibernateProperties());
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5Dialect");//
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "create");
        properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "org.springframework.orm.hibernate5.SpringSessionContext");
        return properties;
    }
}