package ar.edu.iua.iw3;

import ar.edu.iua.iw3.business.ProductBusiness;
import ar.edu.iua.iw3.controllers.ProductRestController;
import ar.edu.iua.iw3.integration.cli2.model.business.IProductCli2Business;
import ar.edu.iua.iw3.integration.cli2.model.business.ProductCli2Business;

import ar.edu.iua.iw3.model.persistence.ProductRepository;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import ar.edu.iua.iw3.business.IProductBusiness;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Iw3Application extends SpringBootServletInitializer implements CommandLineRunner {

	private final ProductRepository productRepository;

    private final ProductRestController productRestController;

    private final ProductCli2Business productCli2Business;

    private final ProductBusiness productBusiness;


    public static void main(String[] args) {
		SpringApplication.run(Iw3Application.class, args);
	}

	@Autowired
	private IProductBusiness productService;

	@Value("${spring.profiles.active:default}")
	private String profile;

	@Value("${spring.jackson.time-zone:-}")
	private String backendTimezone;


    Iw3Application(ProductBusiness productBusiness, ProductCli2Business productCli2Business, ProductRestController productRestController, ProductRepository productRepository) {
        this.productBusiness = productBusiness;
        this.productCli2Business = productCli2Business;
        this.productRestController = productRestController;
        this.productRepository = productRepository;
    }


	@Override
	public void run(String... args) throws Exception {
	String tzId = backendTimezone.equals("-") ? TimeZone.getDefault().getID() : backendTimezone;
		TimeZone.setDefault(TimeZone.getTimeZone(tzId));
		
		log.info("-------------------------------------------------------------------------------------------------------------------");
		log.info("- Initial TimeZone: {} ({})", TimeZone.getDefault().getDisplayName(), TimeZone.getDefault().getID());
		log.info("- Perfil activo {}",profile);
		log.info("-------------------------------------------------------------------------------------------------------------------");


		// log.info("Default ----------------------------------------------------------------------------------------");
		// productCli2Business.listExpired(new Date());

		// log.info("Custom ----------------------------------------------------------------------------------------");
		// productCli2Business.listSlim();

		// log.info("Cantidad de productos de la categoria de id=1: {}", productRepository.countProductsByCategory(3));
		// log.info("Set stock=true product id que no existe, resultado={}", productRepository.setStock(true, 3));


		/*
		log.debug("==============================================================================================");
		log.debug(productService.list().toString());
		log.debug(productService.load(1).toString());
		log.debug(productService.load("Arroz").toString());
		log.debug(productService.load("Leche").toString());
		*/
	}
}

