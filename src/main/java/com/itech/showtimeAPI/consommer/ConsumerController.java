package com.itech.showtimeAPI.consommer;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/consumers")
public class ConsumerController {

    protected final ConsumerService consumerService;

    //GETMAPPING

    @GetMapping
    public List<Consumer> fetchAllConsumers() {

        return consumerService.getAllConsumers();
    }

    @GetMapping(value="/getUserById/{id}")
    public Optional<Consumer> getConsumerById(@PathVariable String id) {

        return consumerService.getConsumerById(id);
    }

    //POSTMAPPING

    @PostMapping(value="/insertConsumer")
    public Consumer insertConsumer(@RequestBody Consumer consumer) {

        return consumerService.insertConsumer(consumer);
    }
}
