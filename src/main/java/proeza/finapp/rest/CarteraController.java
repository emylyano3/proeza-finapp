package proeza.finapp.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarteraController {

    @GetMapping("/cartera")
    public String cartera () {
        return "It works!";
    }
}
