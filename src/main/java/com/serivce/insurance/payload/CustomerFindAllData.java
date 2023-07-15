package com.serivce.insurance.payload;

import java.util.List;

import com.serivce.insurance.entity.Customer;

import lombok.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerFindAllData {
    
        private List<Customer> customer;
        private int page;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    
        
    
      
}
