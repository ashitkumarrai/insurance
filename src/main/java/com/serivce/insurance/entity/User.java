package com.serivce.insurance.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Component
@Builder
public class User {

	
   
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;


	
	
	@Column(unique = true)
	private String username;

  



	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;




	


	
	

	
	@JsonProperty(access = Access.WRITE_ONLY)
  

	private String role;
	
	
	@JsonProperty(access = Access.WRITE_ONLY)
	//for extra email event verifications 
    private boolean enabled;
    

   
	


}
