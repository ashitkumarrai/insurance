package com.serivce.insurance.payload;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serivce.insurance.entity.Policy;
import com.serivce.insurance.entity.Policy.PolicyType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record PolicyCreationForm(@NotNull  Long customerId,

        String policyName,

        PolicyType policyType,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata") LocalDate startDate,

        int duration,
        double coverageAmount,
        List<String> coverageOptions,
        double deductible,
        String beneficiaryName,
        String beneficiaryRelationship,

        @Valid Policy policy,
        @NotNull(message = "agree to terms field must not be null")  @AssertTrue(message="must be agree to terms") Boolean agreedToTerms,
        @NotNull(message = "agree to data processing field must not be null") @AssertTrue(message="must be agree to consent for data processing") Boolean consentForDataProcessing) {
}
        