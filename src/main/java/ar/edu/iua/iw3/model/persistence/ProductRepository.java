package ar.edu.iua.iw3.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Product;
import jakarta.transaction.Transactional;

import java.beans.Transient;
import java.util.Optional;


@Repository // Inyecta funcionalidades
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProduct(String product);

    Optional<Product> findByProductAndIdNot(String product, long id);

    @Query(value = "SELECT count(*) FROM products where id_category=?", nativeQuery = true) // nativeQuery indica SQL nativo
    public Integer countProductsByCategory(long id_category);

    @Transactional
    @Modifying // Le indica al engine que los datos se van a modificar
    @Query(value = "UPDATE products SET stock=? WHERE id=?", nativeQuery = true)
    public int setStock(boolean stock, long idProduct);
}