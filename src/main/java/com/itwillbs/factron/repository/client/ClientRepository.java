package com.itwillbs.factron.repository.client;

import com.itwillbs.factron.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<List<Client>> findByNameContaining(String name);
}
