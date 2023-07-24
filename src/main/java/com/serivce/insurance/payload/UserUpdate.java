package com.serivce.insurance.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

import com.serivce.insurance.entity.Customer.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record UserUpdate(

        @Length(min = 3, max = 15, message = "must have min 3 chars and max 15 ") @Pattern(regexp = "([\\w_\\.]){3,15}", message = "must be alpha-numeric [can contains underscore(_)or dot(.) and @]") String username,

        String fullName,

        String password,

        @Email(message = "Email should be valid")

        String email,

        @Pattern(regexp = "^[2-9]{1}\\d{9}$", message = "phone number not valid") String phone,

        String imageUrl,

        Gender gender,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") @Past LocalDate dateOfBirth) {

}
