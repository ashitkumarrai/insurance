package com.serivce.insurance.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.serivce.insurance.entity.User;




@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	
	 @Query("SELECT p FROM User p WHERE p.username = :query")
	public Optional<User> findByUsername(@Param("query") String username);

    public boolean existsByUsername(String username);

	

	

    
}
