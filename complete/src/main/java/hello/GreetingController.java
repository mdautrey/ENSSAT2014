package hello;

/**
 * Created by mdautrey on 28/09/14.
 */

import model.Individu;
import model.data.InterfaceIndividu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController  {

    @Autowired private InterfaceIndividu interfaceIndividu;

    @RequestMapping(value="/addindividu", method= RequestMethod.GET)
    public String individuForm(Model model) {
        model.addAttribute("individu", new Individu());
        return "addindividu";
    }


    @RequestMapping(value="/addindividu", method=RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute Individu individu, Model model) {
        model.addAttribute("individu", individu);
        return "confirmindividu";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("individu", interfaceIndividu.getIndividu());
        return "greeting";
    }
}
