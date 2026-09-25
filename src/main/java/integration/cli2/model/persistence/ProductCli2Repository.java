package integration.cli2.model.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import integration.cli2.model.ProductCli2;

@Repository
public interface ProductCli2Repository extends JpaRepository<ProductCli2, Long> {
    public List<ProductCli2> findByExpirationDateBeforeOrderByExpirationDateDesc(Date expirationDate);
}