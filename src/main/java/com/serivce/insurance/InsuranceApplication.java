package com.serivce.insurance;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.serivce.insurance.entity.Role;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.repository.RoleRepository;
import com.serivce.insurance.repository.UserRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class InsuranceApplication implements CommandLineRunner {

	@Autowired
	UserRepository ur;

	@Autowired
	RoleRepository rr;




	

	@Override
	public void run(String... args) throws Exception {

		// creating default role
		final String adminRoleName = "admin";
		final String customerRoleName = "customer";

		List<Role> existingRoles = rr.findAll();

		if (existingRoles.isEmpty()) {
			Role adminRole = rr.save(new Role((long) 100, adminRoleName));
			rr.save(new Role(101l,customerRoleName));

			List<Role> roles = new LinkedList<>();
			roles.add(adminRole);
			
			User admin = User.builder().username(adminRoleName).roles(roles).build();
			ur.save(admin);
			log.info("Database empty so Created first Admin Credentials");

		}

	}


	public static void main(String[] args) {
		SpringApplication.run(InsuranceApplication.class, args);
	}

}

