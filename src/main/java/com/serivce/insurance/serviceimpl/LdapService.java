package com.serivce.insurance.serviceimpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.serivce.insurance.entity.User;
import com.serivce.insurance.payload.JwtResponse;
import com.serivce.insurance.repository.UserRepository;
import com.serivce.insurance.util.JwtUtils;

import jakarta.annotation.PostConstruct;

import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.security.MessageDigest;

@Service
@Component
@Slf4j
public class LdapService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.timeout:18000}")
    private long jwtTimeout;

    @Value("${ldap.url}")
    private String ldapUrl;
    @Value("${ldap.port}")
    private String ldapPort;
    @Value("${ldap.managerDN}")
    private String ldapManagerDn;
    @Value("${ldap.managerPassword}")
    private String ldapManagerPassword;
    @Value("${ldap.user.base}")
    private String ldapUserSearchBase;

    @Value("${ldap.user.filter}")
    private String ldapUserSearchFilter;
    @Value("${ldap.group.base}")
    private String groupBase;

    private BaseLdapPathContextSource contextSource;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    

    /**
     * Prepares LDAP context in order to use in authentication processes.
     */
    @PostConstruct
    private void prepareLdapContext() {
        String ldapFullUrl = new StringBuilder(this.ldapUrl)
                .append(":")
                .append(this.ldapPort)
                .toString();
        DefaultSpringSecurityContextSource localContextSource = new DefaultSpringSecurityContextSource(ldapFullUrl);
        localContextSource.setUserDn(this.ldapManagerDn);
        localContextSource.setPassword(this.ldapManagerPassword);
        localContextSource.setBase(this.ldapUserSearchBase);
        localContextSource.afterPropertiesSet();
        this.contextSource = localContextSource;

    }

    public JwtResponse authenticateUser(String username, String password) {

        UserDetails userDetails = this.doLdapSearch(username, password);
        log.info("Authentication of {} successfull! Users groups are: {}", username, userDetails);

        
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> userRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // checking if jwt token exist in db
        String jwtToken = "";
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username " + username + " not found "));
        Optional<String> dbToken = Optional.ofNullable( user.getJwtToken()); 
    
        if (dbToken.isEmpty() || dbToken.get().isBlank() || dbToken.get().equals("") 
                || JwtUtils.isTokenExpired(dbToken.get(), this.jwtSecret)) {

            //if token in db is null or expired create new token and save in db
            

            jwtToken = JwtUtils.createJWTToken(username, this.jwtSecret, this.jwtTimeout, userRoles);
            user.setJwtToken(jwtToken);
            userRepository.save(user);
        }

        else
            jwtToken = dbToken.get();

        return JwtResponse.builder()

                .token(jwtToken)
                .user(userDetails)
                .build();
    }

    public UserDetails doLdapSearch(String username, String password) {
        try {
            // Setup the LDAP authenticator
            BindAuthenticator authenticator = new BindAuthenticator(this.contextSource);
            authenticator.setUserDnPatterns(new String[] { this.ldapUserSearchFilter });

          

            // Perform the LDAP authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authenticator.authenticate(authentication);

            // Get the granted authorities
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            return userDetails;
        } catch (

        BadCredentialsException e) {
            log.error("LDAP authentication failed for {}. Wrong password!", username);
            throw e;
        } catch (UsernameNotFoundException e) {
            log.error("LDAP authentication failed for {}. No such user!", username);
            throw e;

        }
    }

    public String createUserInLdap(String username, String password, String fullName)
            throws javax.naming.NamingException, NoSuchAlgorithmException {
        // Define the distinguished name (DN) for the new user entry
        String uid = "uid=" + username;

        // Set the attributes for the new user entry
        BasicAttributes attrs = new BasicAttributes();
        attrs.put("objectclass", "inetOrgPerson");
        attrs.put("uid", username);
        attrs.put("cn", fullName);
        attrs.put("sn", username);
        attrs.put("userPassword", encodePassword(password)); // Encode the password before storing it

        // Create the new user entry in the LDAP server
        DirContext ctx = this.contextSource.getReadWriteContext();
        ctx.createSubcontext(uid, attrs);
        log.info("user created");
        ctx.close();

        // // Add the new user to the "user" group
        // String userGroupDN = "cn=user," + this.groupBase;
        // ModificationItem[] modificationItems = new ModificationItem[1];
        // modificationItems[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
        //         new BasicAttribute("member", uid + "," + this.ldapUserSearchBase));

        // DirContext groupCtx = this.contextSource.getReadWriteContext();
        // groupCtx.modifyAttributes(userGroupDN, modificationItems);
        // groupCtx.close();

         return username;
    }

    // Helper method to encode the password using SHA-1 and Base64
    private String encodePassword(String password) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha1.digest();
        return "{SHA}" + Base64.getEncoder().encodeToString(digest);
    }

}