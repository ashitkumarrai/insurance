package com.serivce.insurance.payload;


import java.util.List;

import com.serivce.insurance.entity.Policy;
import lombok.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyFindAllData {
    
        private List<Policy> policy;
        private int page;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    
        
    
      
}

