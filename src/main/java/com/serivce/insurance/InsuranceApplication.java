package com.serivce.insurance;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.serivce.insurance.entity.Role;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.entity.Customer.Gender;
import com.serivce.insurance.repository.RoleRepository;
import com.serivce.insurance.repository.UserRepository;
import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class InsuranceApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;




	

	@Override
	public void run(String... args) throws Exception {

		// creating default role
		final String adminRoleName = "admin";
	
		if (userRepository.findAll().isEmpty()) {
          List<Role> assignedRoles = new ArrayList<>();
           roleRepository.save(Role.builder().roleName(adminRoleName).build()).getId();
         assignedRoles.add(roleRepository.findByRoleName(adminRoleName));
            
				
				String dob = "1999-07-24";

				// Define the date format of the input string
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				// Parse the string and create a LocalDate object
				LocalDate localDate = LocalDate.parse(dob, formatter);
				User admin = User.builder().username(adminRoleName)
						.dateOfBirth(localDate)
						.fullName("Admin")
						.email("admin@gmail.com")
						.gender(Gender.MALE)
						.phone("9112489991")

						.roles(assignedRoles).createdAt(Instant.now())
						.updatedAt(Instant.now()).build();
				userRepository.save(admin);
				log.info("Database empty so Created first Admin Credentials");
			}

		

	}


	public static void main(String[] args) {
		SpringApplication.run(InsuranceApplication.class, args);
	}

}

