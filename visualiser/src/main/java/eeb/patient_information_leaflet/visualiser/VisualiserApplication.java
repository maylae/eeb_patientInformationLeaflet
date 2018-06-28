package eeb.patient_information_leaflet.visualiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eeb.patient_information_leaflet.ModelInteraction.ModelInteractor;


@SpringBootApplication @ComponentScan(basePackages = { "eeb.patient_information_leaflet.*"})
public class VisualiserApplication {
	static ModelInteractor mi = null;
	
	
	public static ModelInteractor getModelInteractor() {
		return mi;
	}


	public static void setModelInteractor(ModelInteractor mi) {
		VisualiserApplication.mi = mi;
	}


	public static void main(String[] args) {
		VisualiserApplication.mi = new ModelInteractor();
		SpringApplication.run(VisualiserApplication.class, args);
	}
	
}
