package com.serivce.insurance.controller;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.serivce.insurance.entity.Policy;
import com.serivce.insurance.exceptionhandler.BlankMandatoryFieldException;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.PolicyCreationForm;
import com.serivce.insurance.service.PdfGenerationService;
import com.serivce.insurance.service.PolicyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
@SecurityRequirement(name="Authorization")
public class PolicyController {
    @Autowired
    PolicyService policyService;
     @Autowired
     PdfGenerationService pdfGenerationService;


     @PostMapping("/policy")
     @Tag(name = "1. Customer endpoints")
      @Operation(operationId = "createPolicy",description = "create/apply policy",summary = "CREATE/APPLY POLICY")
   public ResponseEntity<Map<String, String>> createPolicy(@RequestBody @Valid PolicyCreationForm policy)
           throws URISyntaxException, RecordNotFoundException, BlankMandatoryFieldException {

       Policy result = policyService.createPolicy(policy);
       Map<String, String> hasMap = new HashMap<>();
       hasMap.put("response", "created sucessfully!");
       hasMap.put("URI", new URI("/policy?id=" + result.getPolicyId()).toString());

       return new ResponseEntity<>(hasMap, HttpStatus.CREATED);

   }



   @GetMapping("/policies")
   @Tag(name = "2. Admin endpoints")
   @Operation(operationId = "getAllPolicys",summary = "GET LISTS OF ALL POLICIES")
    public ResponseEntity<List<Policy>> getAllPolicys(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "customer.updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {

        Pageable paging = null;
        if (order.contains("asc")) {
            paging = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        Page<Policy> pagedResult = policyService.findAll(paging);

        List<Policy> ansList = pagedResult.getContent();

        
    

        return ResponseEntity.ok().body(ansList);
    }


    @GetMapping("/policy")
    @Tag(name = "1. Customer endpoints")
    @Operation(operationId = "getAllCustomers", responses = {
        @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
       @ApiResponse(responseCode = "403", description = "Forbidden request"),
       @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get Policy by id",summary = "GET POLICY BY ID")
    public ResponseEntity<Object> getPolicyByIdorPolicyName(@RequestParam(defaultValue = "0") Long id,
            @RequestParam(defaultValue = "default") String policyName, @RequestParam(defaultValue = "default") String policyCustomerName) throws RecordNotFoundException {

        if (id != 0l) {
            log.info("request param id catch in getpolicy by id method");
            return ResponseEntity.ok(policyService.findById(id));

        }
         
        if (!policyName.equals("default")) {
            return ResponseEntity.ok(policyService.findByPolicyName(policyName));

        }
        if (!policyCustomerName.equals("default")) {
            return ResponseEntity.ok(policyService.findByPolicyCustomerName(policyCustomerName));

        }
      

        return ResponseEntity.ok(policyService.findById(id));

    }

        
    
    @DeleteMapping("/policy/{id}")
    @Operation(operationId = "getAllCustomers", responses = {
        @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
       @ApiResponse(responseCode = "403", description = "Forbidden request"),
       @ApiResponse(responseCode = "204", description = "DELETE sucessfull") },description = "delete policy by id",summary = "DELETE POLICY BY ID")
    @Tag(name="1. Customer endpoints")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) throws RecordNotFoundException {

        policyService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();

    }
    

    @PatchMapping("/policy/{id}")
    @Operation(operationId = "getAllCustomers",summary = "UPDATE POLICY DETAILS")
       
    @Tag(name="1. Customer endpoints")
    public ResponseEntity<Map<String, String>> partialUpdatePolicy(
            @PathVariable("id") final Long id,
            @RequestBody @Valid PolicyCreationForm policy) throws URISyntaxException, RecordNotFoundException {

        Policy result = policyService.partialUpdatePolicy(id, policy);

        Map<String, String> hasMap = new HashMap<>();
        hasMap.put("response", "updated sucessfully!");
        hasMap.put("URI", new URI("/policy?id=" + result.getPolicyId()).toString());

        return ResponseEntity.created(new URI("/policy?id=" + result.getPolicyId())).body(hasMap);

    }

    
    @GetMapping("/{policyId}/generate-pdf")
    @Operation(operationId = "getAllCustomers",summary = "DOWNLOAD PDF OF APPLIED POLICY DETAILS DOCUMENT")
     
    @Tag(name="1. Customer endpoints")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long policyId) throws RecordNotFoundException {
    // Retrieve the policy from the database based on the policyId

    try {
        Policy policy = policyService.findById(policyId);
        ByteArrayOutputStream baos = pdfGenerationService.generatePdf(policy);

        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=policy.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    } catch (DocumentException e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


}
