package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.Broker;
import proeza.finapp.service.BrokerService;

@CrossOrigin
@RestController
@RequestMapping("api/broker")
public class BrokerController {

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/{id}")
    public Broker get(@PathVariable("id") long id) {
        return this.brokerService.findById(id);
    }

    @PostMapping
    public Broker create(@RequestBody Broker broker) {
        return this.brokerService.create(broker);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody Broker broker) {
        this.brokerService.update(id, broker);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestParam Long idBroker) {
         this.brokerService.delete(idBroker);
    }
}
