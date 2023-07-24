package com.serivce.insurance.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serivce.insurance.entity.Address;
import java.time.LocalDate;
import com.serivce.insurance.entity.Customer.Gender;
import com.serivce.insurance.entity.Customer.MaritalStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record CustomerCreationForm(


                @NotNull(message = "fullName is mandatory") @NotBlank(message = "fullName must not be blank") @Schema(example = "CustomerDemo") String fullName,

                @Email(message = "Email should be valid") @NotNull(message = "email is mandatory") @Schema(example = "customerDemo@gmail.com") String email,

                @Pattern(regexp = "^[2-9]{1}\\d{9}$", message = "phone number not valid") @Schema(example = "9112443255") String phone,
                @Schema(example = "null", type = "string") String imageUrl,
                @NotNull(message = "occupation must not be null") String occupation,
                @NotNull(message = "gender must not be null") Gender gender,
                @NotNull(message = "MaritalStatus must not be null") MaritalStatus maritalStatus,
                @NotNull(message = "numberOfDependents must not be null") @Schema(example = "2") Integer numberOfDependents,

                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") @Past @Schema(example = "12-10-1998", type = "string") LocalDate dateOfBirth,
                @NotNull(message = "address must not be null") Address address,
                @Schema(example = "null") String identitydocumentUrl) {

}
