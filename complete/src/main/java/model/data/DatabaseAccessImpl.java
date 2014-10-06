package model.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

/**
 * Created by mdautrey on 05/10/14.
 */
@Component public class DatabaseAccessImpl implements DatabaseAccess {
    private JdbcTemplate jdbcTemplate;
    public DatabaseAccessImpl(){
        // simple DS for test (not for production!)
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("sa");
        dataSource.setUrl("jdbc:h2:mem");
        dataSource.setPassword("");
        jdbcTemplate =  new JdbcTemplate(dataSource);
    }
    public JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }
}

