package ar.edu.iua.iw3.integration.cli2.model;

import java.sql.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.iua.iw3.model.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli2_products")
@PrimaryKeyJoinColumn(name = "id_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCli2 extends Product {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date expirationDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "cli2_product_component", joinColumns = {
            @JoinColumn(name = "id_product", referencedColumnName = "id_product") }, inverseJoinColumns = {
                    @JoinColumn(name = "id_component", referencedColumnName = "id") })
    private Set<ComponentCli2> components;

}