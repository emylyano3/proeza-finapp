package proeza.finapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.Broker;
import proeza.finapp.repository.BrokerRepository;
import proeza.finapp.repository.ChargeRepository;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class BrokerService {

    @Autowired
    private BrokerRepository brokerRepo;

    @Autowired
    private ChargeRepository cargoRepo;

    public Broker findById(Long id) {
        return this.brokerRepo.findById(id).orElseThrow(this::brokerNotFound);
    }

    private ResourceNotFoundException brokerNotFound() {
        return new ResourceNotFoundException("Broker not found");
    }

    public Broker create(Broker broker) {
        boolean exists = Optional.ofNullable(broker.getId())
                                 .map(id -> this.brokerRepo.findById(id).isPresent())
                                 .orElse(false);
        if (exists) {
            return broker;
        } else {
            broker.getCharges().forEach(c -> c.setBroker(broker));
            return this.brokerRepo.save(broker);
        }
    }

    @Transactional
    public void update(Long id, Broker broker) {
        if (!Objects.equals(id, broker.getId())) {
            throw new IllegalArgumentException("El id del broker a actualizar no coincide con el broker recibido");
        }
        Optional.ofNullable(id)
                .flatMap(_id -> this.brokerRepo.findById(_id))
                .ifPresent(b -> {
                    cargoRepo.deleteAll(b.getCharges());
                    b.getCharges().clear();
                    this.brokerRepo.saveAndFlush(b);
                    broker.getCharges().forEach(c -> c.setBroker(broker)                    );
                    this.brokerRepo.save(broker);
                });
    }

    public void delete(Long id) {
        this.brokerRepo.findById(id).ifPresent(brokerRepo::delete);
    }
}
