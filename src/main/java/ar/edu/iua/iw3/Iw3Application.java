package ar.edu.iua.iw3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ar.edu.iua.iw3.business.IProductBusiness;
import ar.edu.iua.iw3.model.Product;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Iw3Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Iw3Application.class, args);
	}

	@Autowired
	private IProductBusiness productService;

	@Value("${spring.profiles.active:default}")
	private String profile;


	@Override
	public void run(String... args) throws Exception {
	// 	log.debug("\n=================================================");

	// 	// 1. Guardar productos de prueba
	// 	try {
	// 		Product p1 = new Product();
	// 		p1.setProduct("Arroz");
	// 		p1.setPrice(1200.0);
	// 		p1.setStock(true);
	// 		productService.add(p1);

	// 		Product p2 = new Product();
	// 		p2.setProduct("Leche");
	// 		p2.setPrice(1500.0);
	// 		p2.setStock(true);
	// 		productService.add(p2);
	// 	} catch (Exception e) {
	// 		log.warn("Productos ya existentes o error al insertar: {}", e.getMessage());
	// 	}

	// 	// 2. Probar consultas
	// 	log.debug(productService.list().toString());
	// 	log.debug(productService.load(1).toString());
	// 	log.debug(productService.load("Arroz").toString());
	// 	log.debug(productService.load("Leche").toString());

		log.info("Perfil Activo {}", profile);
	}

}

