package proeza.finapp.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proeza.finapp.service.dto.MenuDTO;
import proeza.finapp.service.dto.MenuItemDTO;

import java.util.Locale;

@CrossOrigin
@RestController
@RequestMapping("api/sys")
public class SystemController {

    @GetMapping(value = "/menu/{code}/{user}")
    public MenuDTO getMenu(@PathVariable String code, @PathVariable String user, Locale locale) {
        MenuItemDTO mi = new MenuItemDTO();
        mi.setCode("CAR");
        mi.setText("Cartera");
        mi.setHref("/cartera");
        mi.setEnabled(true);
        mi.setIndex(1);
        MenuDTO m = new MenuDTO();
        m.addItem(mi);
        m.setText("Main menu");
        m.setCode(code);
        return m;
    }
}
