package ar.edu.iua.iw3.integration.cli2.model;

import java.util.Locale.Category;

public interface ProductCli2SlimView {

    Long getId();
    String getProduct();
    Double getPrecio();

    Category getCategory();
    interface Category {
        String getCategory();
    }
}
