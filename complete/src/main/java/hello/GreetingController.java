package hello;

/**
 * Created by mdautrey on 28/09/14.
 */

import model.data.InterfaceIndividu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController  {

    @Autowired private InterfaceIndividu interfaceIndividu;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("individu", interfaceIndividu.getIndividu());
        return "greeting";
    }
}
