package com.serivce.insurance.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serivce.insurance.entity.Address;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

import com.serivce.insurance.entity.Customer.Gender;
import com.serivce.insurance.entity.Customer.MaritalStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record CustomerCreationForm(
    
        @Length(min = 3,max=15, message = "must have min 3 chars and max 15 ")
	    @Pattern(regexp = "([\\w_\\.]){3,15}", message = "must be alpha-numeric [can contains underscore(_)or dot(.) and @]")
        String username,
        @NotNull(message = "fullName is mandatory") @NotBlank(message = "fullName must not be blank")
        String fullName,

        String password,

         @Email(message = "Email should be valid")
         @NotNull(message = "email is mandatory")
        String email,

        @Pattern(regexp="^[2-9]{2}\\d{8}$",message= "phone number not valid")
        String phone,

         String imageUrl,
         @NotNull(message = "occupation must not be null")
                String occupation,
         @NotNull(message = "gender must not be null")
        Gender gender,
         @NotNull(message = "MaritalStatus must not be null")
                MaritalStatus maritalStatus,
         @NotNull (message = "numberOfDependents must not be null")
        Integer numberOfDependents,
        
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        @Past
                LocalDate dateOfBirth,
         @NotNull(message = "address must not be null") 
        Address address, String identitydocumentUrl) {
    
}
