package com.example.yugabyteftw;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class YugabyteftwApplication {

    public static void main(String[] args) {
        SpringApplication.run(YugabyteftwApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CustomerService service) {
        return args -> service
                .reset("A", "B", "C", "D", "E")
                .doOnNext(c -> Assert.isTrue(Character.isUpperCase(c.name().charAt(0)), () -> "the first letter must be uppercase!"))
                .subscribe(System.out::println);
    }

}

@Service
class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Flux<Customer> reset(String... names) {
        var delete = this.customerRepository.deleteAll();
        var write = Flux.fromArray(names)
                .map(name -> new Customer(null, name))
                .flatMap(this.customerRepository::save);
        return delete.thenMany(write);
    }

}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}