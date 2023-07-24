package com.serivce.insurance.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import com.serivce.insurance.entity.Customer.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Builder
public class UserCreationForm {

       @Length(min = 3, max = 15, message = "must have min 3 chars and max 15 ")
       @Pattern(regexp = "([\\w_\\.]){3,15}", message = "must be alpha-numeric [can contains underscore(_)or dot(.) and @]")
       @Schema(example = "userDemo")
       @NotNull(message = "username must not null")
       private String username;

       @NotNull(message = "fullName is mandatory")
       @NotBlank(message = "fullName must not be blank")
       @Schema(example = "UserDemo")
       private String fullName;
       @Schema(example = "userDemo123")
       private String password;

       @Email(message = "Email should be valid")
       @NotNull(message = "email is mandatory")
       @Schema(example = "userDemo@gmail.com")
       private String email;

       @Pattern(regexp = "^[2-9]{1}\\d{9}$", message = "phone number not valid")
       @Schema(example = "9112443255")
       private String phone;
       @Schema(example = "null", type = "string")
       private String imageUrl;

       @NotNull(message = "gender must not be null")
       private Gender gender;

       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
       @Past
       @Schema(example = "12-10-1998", type = "string")
       private LocalDate dateOfBirth;
       @Schema(type = "array", example = "[\"user\"]")
       private List<String> roles;

}
