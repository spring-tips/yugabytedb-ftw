package com.example.yugabyteftw;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class YugabyteftwApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(YugabyteftwApplication.class, args);
        Thread.sleep(1000);
    }

    @Bean
    ApplicationRunner runner(CustomerRepository repository,
                             CustomerService service) {
        return args -> service.reset("Karthik", "Bill", "Amey", "Josh")
                .subscribe(System.out::println);
    }

    @Bean
    RouterFunction<ServerResponse> routes(CustomerRepository repository) {
        return route()
                .GET("/customers", request -> ok().body(repository.findAll(), Customer.class))
                .build();
    }
}

@Service
class CustomerService {

    private final TransactionalOperator transactionalOperator;

    private final CustomerRepository customerRepository;

    CustomerService(TransactionalOperator transactionalOperator, CustomerRepository customerRepository) {
        this.transactionalOperator = transactionalOperator;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Flux<Customer> reset(String... names) {
        return this.customerRepository //
                .deleteAll() //
                .thenMany(Flux
                        .fromArray(names)
                        .map(name -> new Customer(null, name))
                        .flatMap(this.customerRepository::save)
                )
                .doOnNext(c -> Assert.isTrue(Character.isUpperCase(c.name().charAt(0)), () -> "the name should start with a capital letter"));

    }


}

record Customer(@Id Integer id, String name) {
}


interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

