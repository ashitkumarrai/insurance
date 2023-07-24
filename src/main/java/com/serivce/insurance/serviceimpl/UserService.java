package com.serivce.insurance.serviceimpl;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.NamingException;
import org.springframework.stereotype.Component;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.entity.Role;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.payload.UserUpdate;
import com.serivce.insurance.payload.UserCreationForm;
import com.serivce.insurance.repository.UserRepository;

import com.serivce.insurance.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class UserService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    LdapService portalUserService;

    @Autowired
    RoleRepository roleRepository;

   
    public User createUser(UserCreationForm userForm)
            throws NamingException, NoSuchAlgorithmException, javax.naming.NamingException {
        List<Role> assignedRoles = new ArrayList<>();
        for (String rName : userForm.getRoles()) {
            //if role does not exist in db then create new role and assign user to this new role
            
            if (!roleRepository.existsByRoleName(rName)) {
                roleRepository.save(Role.builder().roleName(rName).build()).getId();
            }
            assignedRoles.add(roleRepository.findByRoleName(rName));
            
            
        }
        
        User user = User.builder()
                
                .fullName(userForm.getFullName())
                .email(userForm.getEmail())
                .phone(userForm.getPhone())
                .imageUrl(userForm.getImageUrl())
                .username(userForm.getUsername())
                
                
                .gender(userForm.getGender())
               
             
                .dateOfBirth(userForm.getDateOfBirth())
                
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .roles(assignedRoles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("saved user in database");
        try {
            portalUserService.createUserInLdap(userForm.getUsername(), userForm.getPassword(),
                    userForm.getFullName());
        } catch (Exception e) {
            userRepository.delete(savedUser); //if user not creating  in ldap then dont save in mysql also
            log.info("rollbacked the saved user from database because of the exception came in ldap");
            throw e;
        }

        return savedUser;

    }


    public Page<User> findAll(Pageable paging) {
        return userRepository.findAll(paging);
    }

  
    public User findById(Long id) throws RecordNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("user of id " + id + " is not found in db"));
    }

   
    public void deleteById(Long id) throws RecordNotFoundException {
        findById(id);
        userRepository.deleteById(id);
    }


    public User partialUpdateUser(Long userId ,UserUpdate user) throws RecordNotFoundException {
        User existingUser = findById(userId);

        updateUserFields(existingUser, user);

        existingUser.setUpdatedAt(Instant.now());

        return userRepository.save(existingUser);
    }



    private void updateUserFields(User user, UserUpdate user2) {
        if (isNotBlank(user2.fullName())) {
            user.setFullName(user2.fullName());
        }
        if (isNotBlank(user2.email())) {
            user.setEmail(user2.email());
        }
        if (isNotBlank(user2.phone())) {
            user.setPhone(user2.phone());
        }
        if (isNotBlank(user2.imageUrl())) {
            user.setImageUrl(user2.imageUrl());
        }
       

        if (user2.gender() != null) {
            user.setGender(user2.gender());
        }
      
       
        if (user2.dateOfBirth() != null) {
            user.setDateOfBirth(user2.dateOfBirth());
        }

        if (user2.username() != null) {
            user.setUsername(user2.username());
        }
        
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isEmpty();
    }

    
}




