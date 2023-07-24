package com.serivce.insurance.payload;


import java.util.List;

import com.serivce.insurance.entity.User;

import lombok.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFindAllData {
    
        private List<User> user;
        private int page;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    
        
    
      
}

