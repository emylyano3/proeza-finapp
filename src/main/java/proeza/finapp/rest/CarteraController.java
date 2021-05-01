package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import proeza.finapp.entities.Cartera;
import proeza.finapp.entities.Compra;
import proeza.finapp.entities.Venta;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.service.CarteraService;

@CrossOrigin
@RestController
@RequestMapping("api/cartera")
public class CarteraController {

    @Autowired
    private CarteraRepository carteraRepo;

    @Autowired
    private CarteraService carteraService;

    @GetMapping("/{id}")
    public Cartera cartera(@PathVariable("id") long id) {
        return carteraRepo.findById(id);
    }

    @PostMapping("/venta")
    public Cartera venta(@RequestBody Venta venta) {
        return carteraService.venta(venta);
    }

    @PostMapping("/compra")
    public Cartera compra(@RequestBody Compra compra) {
        return carteraService.compra(compra);
    }
}
