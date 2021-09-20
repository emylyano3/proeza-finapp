package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.Cartera;
import proeza.finapp.repository.CarteraRepository;
import proeza.finapp.rest.dto.CompraDTO;
import proeza.finapp.rest.dto.VentaDTO;
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

    @PostMapping
    public Cartera crear(@RequestBody Cartera cartera) {
        return carteraService.crear(cartera);
    }

    @PostMapping("/{cartera_id}/venta")
    public Cartera venta(@RequestBody VentaDTO venta) {
        return carteraService.venta(venta);
    }

    @PostMapping("/{cartera_id}/compra")
    public Cartera compra(@RequestBody CompraDTO compra) {
        return carteraService.compra(compra);
    }
}
