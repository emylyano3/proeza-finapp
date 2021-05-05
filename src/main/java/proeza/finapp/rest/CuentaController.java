package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proeza.finapp.entities.Cuenta;
import proeza.finapp.entities.Deposito;
import proeza.finapp.entities.Extraccion;
import proeza.finapp.service.CuentaService;

@CrossOrigin
@RestController
@RequestMapping("api/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/{id}")
    public Cuenta cuenta(@PathVariable("id") long id) {
        return cuentaService.findById(id);
    }

    @PostMapping("/extraccion")
    public Cuenta extraccion(@RequestBody Extraccion extraccion) {
        return cuentaService.extraccion(extraccion);
    }

    @PostMapping("/deposito")
    public Cuenta deposito(@RequestBody Deposito deposito) {
        return cuentaService.deposito(deposito);
    }
}
