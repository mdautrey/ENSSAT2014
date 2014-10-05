package model.data;

import model.Individu;
import org.springframework.stereotype.Component;

/**
 * Created by mdautrey on 29/09/14.
 */

@Component
public class PersistIndividu implements InterfaceIndividu {
    public Individu getIndividu(){
        return new Individu();
    }
}
