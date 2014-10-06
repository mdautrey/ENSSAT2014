package model.data;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by mdautrey on 05/10/14.
 * une interface de type jdbc template
 * qui permet d isoler la logique d acces a la base de donnees
 * du reste de l application
 */
public interface DatabaseAccess {
    // on recupere le JbcTemplate correspondant a la base de donnees
    // de maniere tres basique
    public JdbcTemplate getJdbcTemplate();
}
