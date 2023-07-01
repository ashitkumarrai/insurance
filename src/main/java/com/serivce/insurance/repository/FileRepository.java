package com.serivce.insurance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.serivce.insurance.entity.File;

import org.springframework.stereotype.Repository;







@Repository
public interface FileRepository extends JpaRepository<File, String> {
    
}
