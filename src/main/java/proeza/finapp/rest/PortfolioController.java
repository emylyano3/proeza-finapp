package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.AssetBreadcrumb;
import proeza.finapp.entities.Portfolio;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SellDTO;
import proeza.finapp.service.PortfolioService;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/{cartera_id}/asset-breadcrumb")
    public List<AssetBreadcrumb> getAssetBreadcrumb(
            @PathVariable("cartera_id") Long portfolioId,
            @RequestParam("ticker") String ticker){
        return this.portfolioService.getAssetBreadCrumb(portfolioId, ticker);
    }

    @PostMapping("/{cartera_id}/venta")
    public Portfolio sale(@Valid @RequestBody SellDTO sellDTO) {
        return portfolioService.sell(sellDTO);
    }

    @PostMapping("/{cartera_id}/compra")
    public Portfolio buyout(@Valid @RequestBody BuyDTO buyDTO) {
        return portfolioService.buy(buyDTO);
    }
}
