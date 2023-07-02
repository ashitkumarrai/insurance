package com.serivce.insurance.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"}, ignoreUnknown = true)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

 
   @OneToOne(cascade = CascadeType.ALL)
    private User user;
   
    @Column(nullable = false)
    private String fullName;

    private String email;


    private String phone;
	
	
    private String imageUrl;
    private String identitydocumentUrl;



    // Additional variables specific to the insurance policy portal system
    private String occupation;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private Integer numberOfDependents;

 

  
  
     
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
    private LocalDate dateOfBirth;

    @Embedded
    private Address address;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum MaritalStatus {
        SINGLE,
        MARRIED,
        DIVORCED,
        WIDOWED,
        OTHER
    }

      @Column(nullable = false, updatable = false)
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS", timezone = "Asia/Kolkata")
      private Instant createdAt;

      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS", timezone = "Asia/Kolkata")
      @Column(nullable = false)
      private Instant updatedAt;




  
   
      @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
      @JsonIgnoreProperties({ "customer" })
      
    private List<Policy> policies = new ArrayList<>();
  
}
