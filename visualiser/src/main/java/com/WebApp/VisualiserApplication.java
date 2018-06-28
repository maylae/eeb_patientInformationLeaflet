package com.WebApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ModelInteraction.ModelInteractor;


@SpringBootApplication 
public class VisualiserApplication {
	static ModelInteractor mi = null;
	
	
	public static ModelInteractor getModelInteractor() {
		return mi;
	}


	public static void main(String[] args) {
		VisualiserApplication.mi = new ModelInteractor();
		SpringApplication.run(VisualiserApplication.class, args);
	}
	
}
