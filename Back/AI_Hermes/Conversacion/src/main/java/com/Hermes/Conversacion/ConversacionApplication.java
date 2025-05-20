package com.Hermes.Conversacion;

import com.Hermes.Conversacion.Service.GeminiService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConversacionApplication {

	public static void main(String[] args) {

		SpringApplication.run(ConversacionApplication.class, args);

		GeminiService gemini = new GeminiService();
		String respuesta = gemini.generarContenido("¿Quien fue Albert Einstein?");
		System.out.println(respuesta);
	}

}
