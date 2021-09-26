package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.Portfolio;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SaleDTO;
import proeza.finapp.service.PortfolioService;

@CrossOrigin
@RestController
@RequestMapping("api/cartera")
public class PortfolioController {

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/{id}")
    public Portfolio get(@PathVariable("id") long id) {
        return portfolioRepo.findById(id);
    }

    @PostMapping
    public Portfolio create(@RequestBody Portfolio portfolio) {
        return portfolioService.create(portfolio);
    }

    @PostMapping("/{cartera_id}/venta")
    public Portfolio sale(@RequestBody SaleDTO saleDTO) {
        return portfolioService.sale(saleDTO);
    }

    @PostMapping("/{cartera_id}/compra")
    public Portfolio buy(@RequestBody BuyDTO buy) {
        return portfolioService.buy(buy);
    }
}
