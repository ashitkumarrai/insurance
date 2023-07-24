package com.serivce.insurance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serivce.insurance.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
    public boolean existsByRoleName(String roleName);

    public Role findByRoleName(String roleName);
}
