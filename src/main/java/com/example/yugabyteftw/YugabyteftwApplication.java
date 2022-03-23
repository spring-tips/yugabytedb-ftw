package com.example.yugabyteftw;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class YugabyteftwApplication {

    public static void main(String[] args)  throws Exception {
        SpringApplication.run(YugabyteftwApplication.class, args);
        Thread.sleep(1000);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository customerRepository) {
        return args -> Flux.just("A", "B", "C")
                .map(name -> new Customer(null, name))
                .flatMap(customerRepository::save)
                .subscribe(System.out::println);
    }

}

record Customer(@Id Integer id, String name) {
}


interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

