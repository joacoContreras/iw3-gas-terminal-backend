package ar.edu.iua.iw3.model;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Marca el atributo id como PK auto-incremental
    private Long id;

    @Column(length = 100, unique = true)
    private String product;

    private double price;

    @Column(columnDefinition = "tinyint default 1")
    private boolean stock = true;

    @ManyToOne
    @JoinColumn(name="id_category", nullable = true)
    private Category category;

    @Override
    public String toString() {
        return String.format("id=%s product=%s  price=%s stock=%s", this.getId(), this.getProduct(), this.getPrice(), this.isStock());
    }
}
