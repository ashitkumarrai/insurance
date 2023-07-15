package com.serivce.insurance.payload;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serivce.insurance.entity.Policy.PolicyType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record PolicyCreationForm(
@Schema(example = "Demo Policy")
        String policyName,

        PolicyType policyType,
         @Schema(example = "12-10-2024",type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata") LocalDate startDate,
         @Schema(example = "3")
                int duration,
        @Schema(example = "300000")
        double coverageAmount,
                List<String> coverageOptions,
        @Schema(example = "3000")
        double deductible,
                String beneficiaryName,
        @Schema(example = "self")
        String beneficiaryRelationship,

        @NotNull(message = "agree to terms field must not be null")  @AssertTrue(message="must be agree to terms") Boolean agreedToTerms,
        @NotNull(message = "agree to data processing field must not be null") @AssertTrue(message="must be agree to consent for data processing") Boolean consentForDataProcessing) {
}
        