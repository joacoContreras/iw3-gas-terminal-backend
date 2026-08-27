package ar.edu.iua.iw3.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Product;

@Repository // Inyecta funcionalidades
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}