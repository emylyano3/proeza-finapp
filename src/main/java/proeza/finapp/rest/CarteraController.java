package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.Portfolio;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.CompraDTO;
import proeza.finapp.rest.dto.VentaDTO;
import proeza.finapp.service.CarteraService;

@CrossOrigin
@RestController
@RequestMapping("api/cartera")
public class CarteraController {

    @Autowired
    private PortfolioRepository carteraRepo;

    @Autowired
    private CarteraService carteraService;

    @GetMapping("/{id}")
    public Portfolio get(@PathVariable("id") long id) {
        return carteraRepo.findById(id);
    }

    @PostMapping
    public Portfolio create(@RequestBody Portfolio portfolio) {
        return carteraService.crear(portfolio);
    }

    @PostMapping("/{cartera_id}/venta")
    public Portfolio sale(@RequestBody VentaDTO venta) {
        return carteraService.sale(venta);
    }

    @PostMapping("/{cartera_id}/compra")
    public Portfolio buy(@RequestBody CompraDTO compra) {
        return carteraService.buy(compra);
    }
}
