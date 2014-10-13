package hello;

/**
 * Created by mdautrey on 13/10/14.
 */

import model.Individu;
import model.data.InterfaceIndividu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class RestGreetingController {
    @Autowired
    private InterfaceIndividu interfaceIndividu;

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/individu")
    public Individu greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        return interfaceIndividu.getIndividu();
    }
}

