package model.data;

import model.Individu;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mdautrey on 29/09/14.
 */

@Component
public class PersistIndividu implements InterfaceIndividu, InitializingBean {

    @Autowired private DatabaseAccess databaseAccess;

    @Override public void afterPropertiesSet() throws Exception{
        System.out.println("code d initialisation");
        this.databaseAccess.getJdbcTemplate().execute("drop table individus if exists");
        this.databaseAccess.getJdbcTemplate().execute("create table individus(" +
                "id serial, prenom varchar(255), nom varchar(255), civilite varchar(255))");

        String[] names = "Sam Sick Mister;Jeff Dean Mister;Jenny Blasch Madam; Jean-Pierre Robert Monsieur".split(";");
        for (String fullname : names) {
            String[] name = fullname.split(" ");
            this.databaseAccess.getJdbcTemplate().update(
                    "INSERT INTO individus(prenom,nom, civilite) values(?,?,?)",
                    name[0], name[1], name[2]);
        }
    }

    public Individu getIndividu(){
        List<Individu> results = databaseAccess.getJdbcTemplate().query(
                "select * from individus",
                new RowMapper<Individu>() {
                    @Override
                    public Individu mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Individu(rs.getLong("id"), rs.getString("prenom"),
                                rs.getString("nom"));
                    }
                });
        return results.get(1);
    }



}
