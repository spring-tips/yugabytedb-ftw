package com.example.yugabyteftw;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class YugabyteftwApplication {

    public static void main(String[] args) {
        SpringApplication.run(YugabyteftwApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository customerRepository) {
        return args -> {

            var names = Flux.just("A", "B", "C")
                    .map(name -> new Customer(null, name))
                    .flatMap(customerRepository::save);
            names.subscribe(System.out::println);

        };
    }

}

record Customer(Integer id, String name) {
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
