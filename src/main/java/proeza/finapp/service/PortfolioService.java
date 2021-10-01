package proeza.finapp.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import proeza.finapp.domain.AssetBreadcrumb;
import proeza.finapp.domain.Instrument;
import proeza.finapp.domain.Portfolio;
import proeza.finapp.exception.BusinessException;
import proeza.finapp.exception.ExceptionFactory;
import proeza.finapp.patterns.command.Invoker;
import proeza.finapp.repository.AssetBreadcrumbRepository;
import proeza.finapp.repository.InstrumentRepository;
import proeza.finapp.repository.PortfolioRepository;
import proeza.finapp.rest.dto.BuyDTO;
import proeza.finapp.rest.dto.SellDTO;
import proeza.finapp.rest.translator.BuyTranslator;
import proeza.finapp.rest.translator.SaleTranslator;
import proeza.finapp.service.command.BuyCommand;
import proeza.finapp.service.command.SellCommand;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Log4j2
@Service
@Transactional
public class PortfolioService {

    @Autowired
    private SaleTranslator saleTranslator;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private BuyTranslator buyTranslator;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private InstrumentRepository instrumentRepo;

    @Autowired
    private AssetBreadcrumbRepository assetBreadcrumbRepo;

    public Portfolio create(Portfolio portfolio) {
        Objects.requireNonNull(portfolio);
        boolean exists = Optional.ofNullable(portfolio.getId())
                                 .map(portfolioRepo::findById)
                                 .isPresent();
        return exists ? portfolio : portfolioRepo.save(portfolio);
    }

    public List<AssetBreadcrumb> getAssetBreadCrumb(Long portfolioId, String ticker) {
        Portfolio portfolio = portfolioRepo.findById(portfolioId)
                                           .orElseThrow(entityNotFound("Portfolio", portfolioId));
        Instrument instrument = this.instrumentRepo.findByTicker(ticker)
                                                   .orElseThrow(entityNotFound("Instrument", ticker));
        return assetBreadcrumbRepo.findByAssetPortfolioAndAssetInstrumentAndRemainsGreaterThan(portfolio, instrument, 0);
    }

    private Supplier<EntityNotFoundException> entityNotFound(String entityName, Object key) {
        throw new EntityNotFoundException(String.format("%s not found: %s", entityName, key));
    }

    public Portfolio sell(SellDTO sellDTO) throws BusinessException {
        Objects.requireNonNull(sellDTO);
        SellCommand command = this.appContext.getBean(SellCommand.class)
                                             .withSale(saleTranslator.toDomain(sellDTO));
        return new Invoker().invoke(command)
                            .peek(p -> log.info("Sale executed ok with data: {}", sellDTO))
                            .peekLeft(e -> log.info("Sale not executed due to error: {}", e.getMessage()))
                            .getOrElseThrow(ExceptionFactory::newBusinessException);
    }

    public Portfolio buy(BuyDTO buyDTO) throws BusinessException {
        Objects.requireNonNull(buyDTO);
        BuyCommand command = this.appContext.getBean(BuyCommand.class)
                                             .withBuyout(buyTranslator.toDomain(buyDTO));
        return new Invoker().invoke(command)
                            .peek(p -> log.info("Buyout executed ok with data: {}", buyDTO))
                            .peekLeft(e -> log.info("Buyout not executed due to error: {}", e.getMessage()))
                            .getOrElseThrow(ExceptionFactory::newBusinessException);
    }
}
