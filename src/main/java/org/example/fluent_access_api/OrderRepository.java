package org.example.fluent_access_api;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long>, ReactiveSortingRepository<Order, Long> {

}
