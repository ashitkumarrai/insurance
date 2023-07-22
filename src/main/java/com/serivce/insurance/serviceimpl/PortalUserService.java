package com.serivce.insurance.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.serivce.insurance.entity.User;
import com.serivce.insurance.payload.JwtResponse;
import com.serivce.insurance.util.Constants;
import com.serivce.insurance.util.JwtUtils;

import jakarta.annotation.PostConstruct;

import javax.naming.NameAlreadyBoundException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.security.MessageDigest;


@Service
@Component
@Slf4j
public class PortalUserService implements UserDetailsService {

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


    /**
     * Locates the user from remote LDAP and returns the information wrapped in a {@link PortalUserPrincipal} object.
     *
     * @param username: Username to be queried from LDAP. Mostly, case-insensitive.
     * @return {@link UserDetails}
     * @throws {@link UsernameNotFoundException} when there is no entry matches with <code>username</code>.
     * @throws {@link IncorrectResultSizeDataAccessException} when there is more than one entry matches with <code>username</code>.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            log.info("Searching LDAP for user {}", username);
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(new String[]{Constants.LDAP_ATTRIBUTE_ISMEMBEROF, Constants.LDAP_ATTRIBUTE_UID});
            SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
            template.setSearchControls(searchControls);
            

            DirContextOperations searchResult = template.searchForSingleEntry("", this.ldapUserSearchFilter,
                    new String[] { username });
        

            List<String> grantedAuthorities = new ArrayList<>(this.getGrantedAuthorities(searchResult));
            log.info("User {} retrieved. User's roles are: {}", username, grantedAuthorities);

            return new UserPrincipal(User.builder()
                    .username(username)
                    .grantedAuthorities(grantedAuthorities)
                    .build());
        } catch (IncorrectResultSizeDataAccessException ex) {
            log.error("Unexpected result size returned from LDAP for search for user {}", username);

            if (ex.getActualSize() == 0) {
                throw new UsernameNotFoundException("User " + username + " not found in LDAP.");
            } else {
                throw ex;
            }
        }
     
    }

    /**
     * Authenticates the user from remote LDAP by using <code>username</code> and <code>password</code> credentials provided.
     * Populates the {@link SecurityContextHolder} with an {@link Authentication} object.
     *
     * @param username
     * @param password
     *
     * @return An {@link AuthResponse} object populated with user detail information and a JWT token to be used in following calls.
     */
    public JwtResponse authenticateUser(String username, String password) {
        Assert.isTrue(StringUtils.isNotBlank(username), "Username should not left blank!");
        Assert.isTrue(StringUtils.isNotBlank(password), "Password should not left blank!");

        List<String> grantedAuthorities = this.doLdapSearch(username, password);
        log.info("Authentication of {} successfull! Users groups are: {}", username, grantedAuthorities);

        UserPrincipal portalUserPrincipal = new UserPrincipal(User.builder()
                .username(username)
                .grantedAuthorities(grantedAuthorities)
                .build());
        Authentication authentication = new UsernamePasswordAuthenticationToken(portalUserPrincipal, null, portalUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> userRoles = portalUserPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //check if it token exist in mysql db
          

        return JwtResponse.builder()
               
        .token(JwtUtils.createJWTToken(username, this.jwtSecret, this.jwtTimeout, userRoles))
                .user(portalUserPrincipal)
                .build();
    }

    private List<String> doLdapSearch (String username, String password) {
        try {
            UserPrincipal portalUserPrincipal = new UserPrincipal(User.builder().username(username).build());
            Authentication authentication = new UsernamePasswordAuthenticationToken(portalUserPrincipal, password);
          
            PasswordComparisonAuthenticator passwordComparisonAuthenticator = new PasswordComparisonAuthenticator(this.contextSource);
            passwordComparisonAuthenticator.setPasswordEncoder(new LdapShaPasswordEncoder());
        
            passwordComparisonAuthenticator.setUserDnPatterns(new String[]{this.ldapUserSearchFilter });
           
    
                    DirContextOperations authenticationResult = passwordComparisonAuthenticator
                            .authenticate(authentication);


                          
            

                    return this.getGrantedAuthorities(authenticationResult);
        }
       
        catch (BadCredentialsException e) {
            log.error("LDAP authentication failed for {}. Wrong password!", username);
            throw e;
        } catch (UsernameNotFoundException e) {
            log.error("LDAP authentication failed for {}. No such user!", username);
            throw e;

        }
    }

    /**
     * Extracts the list of granted authorities from LDAP query result.
     *
     * @return A {@link List} of users roles defined in LDAP.
     */
   /**
 * Extracts the list of granted authorities (groups) from LDAP query result.
 *
 * @param ldapResult The LDAP query result for the user.
 * @return A list of user's roles (groups) defined in LDAP.
 */
   private List<String> getGrantedAuthorities(DirContextOperations ldapResult) {
       // Search for the 'member' attribute in the 'ou=groups' entries
       String userDN = ldapResult.getNameInNamespace();
       SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
       String groupFilter = "(&(objectClass=groupOfNames)(member=" + userDN + "))";
       SearchControls searchControls = new SearchControls();
       searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

       // Search for groups where the userDN is a member
       return template.search(
               this.groupBase, groupFilter, searchControls,
               (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());

   }


    /**
     * Creates a new customer in the LDAP server and adds them to the "customer" group.
     *
     * @param username The username of the customer.
     * @param password The password of the customer (plain text).
     * @param fullName The full name of the customer.
     * @param email    The email address of the customer.
     * @return The created customer's username.
     * @throws NamingException if there is an issue while creating the customer.
     * @throws javax.naming.NamingException
     * @throws NoSuchAlgorithmException
     */
    public String createCustomerInLdap(String username, String password, String fullName) throws javax.naming.NamingException, NoSuchAlgorithmException {
        // Define the distinguished name (DN) for the new customer entry
        String newUserDN = "uid=" + username;
        log.info("userDn: " + newUserDN);

        // Set the attributes for the new customer entry
        BasicAttributes attrs = new BasicAttributes();
        attrs.put("objectclass", "inetOrgPerson");
        attrs.put("uid", username);
        attrs.put("cn", fullName);
        attrs.put("sn", username);
        attrs.put("userPassword", encodePassword(password)); // Encode the password before storing it

        // Create the new customer entry in the LDAP server
        DirContext ctx = this.contextSource.getReadWriteContext();
        ctx.createSubcontext(newUserDN, attrs);
        log.info("customer created");
        ctx.close();

        // Add the new customer to the "customer" group
        String customerGroupDN = "cn=customer," + this.groupBase;
        ModificationItem[] modificationItems = new ModificationItem[1];
        modificationItems[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", newUserDN));

        DirContext groupCtx = this.contextSource.getReadWriteContext();
        groupCtx.modifyAttributes(customerGroupDN, modificationItems);
        groupCtx.close();

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