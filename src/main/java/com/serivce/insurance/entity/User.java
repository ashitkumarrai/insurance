package com.serivce.insurance.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.serivce.insurance.entity.Customer.Gender;

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
public class User implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;


	
	@Column(nullable = false)
    private String fullName;

    private String email;


	private String phone;
	
	private String imageUrl;

	@Column(unique = true)
	private String username;

	@Enumerated(EnumType.STRING)
    private Gender gender;
  
       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
    private LocalDate dateOfBirth;

         //agent, promoter, Policy Manager/Administrator, etc
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private List<Role> roles;
	
	
      @Column(nullable = false, updatable = false)
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS", timezone = "Asia/Kolkata")
      private Instant createdAt;

      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS", timezone = "Asia/Kolkata")
      @Column(nullable = false)
      private Instant updatedAt;
	

      @JsonProperty(access = Access.WRITE_ONLY)
	private String jwtToken;

   
	


}
