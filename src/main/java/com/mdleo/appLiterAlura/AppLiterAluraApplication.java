package com.mdleo.appLiterAlura;


import com.mdleo.appLiterAlura.principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppLiterAluraApplication implements CommandLineRunner {

	private final Principal principal;

	@Autowired
	public AppLiterAluraApplication(Principal principal) {
		this.principal = principal;
	}

	public static void main(String[] args) {
		SpringApplication.run(AppLiterAluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		principal.showMenu();
	}
}
