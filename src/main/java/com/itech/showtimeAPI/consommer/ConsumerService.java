package com.itech.showtimeAPI.consommer;

import com.itech.showtimeAPI.repository.ConsumerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ConsumerService {

    private ConsumerRepository  consumerRepository;

    public List<Consumer> getAllConsumers() {

        return consumerRepository.findAll();
    }

    public Optional<Consumer> getConsumerById(String id) {

        return consumerRepository.findById(id);
    }

    public Consumer insertConsumer(Consumer consumer) {

        return consumerRepository.insert(consumer);
    }
}
