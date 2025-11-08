package com.assetneuron.whatsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = {
		"com.assetneuron.whatsapp.model",
		"com.assetneuron.whatsapp.common.persistence"
})
@ComponentScan(basePackages = {
    "com.assetneuron.whatsapp",
    "com.assetneuron.whatsapp.common"
})
public class WhatsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatsAppApplication.class, args);
	}

}
