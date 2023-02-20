package com.argusoft.who.emcare.web.config;

import com.argusoft.who.emcare.web.user.dto.AccessTokenForUser;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@Component
public class KeyCloakConfig {

    @Autowired
    HttpServletRequest request;

    Keycloak keycloak = null;


    public static  String SERVER_URL;
    @Value("${KEYCLOACK_SERVER_URL:https://emcare.argusoft.com/auth}")
    public void setUrlStatic(String KEYCLOACK_SERVER_URL){
        KeyCloakConfig.SERVER_URL = KEYCLOACK_SERVER_URL;
    }
    public  static String CLIENT_SECRET;
    @Value("${KEYCLOACK_CLIENT_SECRET:b5a37bde-8d54-4837-a8dc-12e1f808e26e}")
    public void setSecretStatic(String KEYCLOACK_CLIENT_SECRET){
        KeyCloakConfig.CLIENT_SECRET = KEYCLOACK_CLIENT_SECRET;
    }
    public  static String CLIENT_ID;
    @Value("${KEYCLOACK_CLIENT_ID:emcare}")
    public void setIdStatic(String KEYCLOACK_CLIENT_ID){
        KeyCloakConfig.CLIENT_ID = KEYCLOACK_CLIENT_ID;
    }
    
    public static  String REALM;
    @Value("${KEYCLOACK_REALM:emcare}")
    public void setRealmStatic(String KEYCLOACK_REALM){
        KeyCloakConfig.REALM = KEYCLOACK_REALM;
    }

    public static  String USER_NAME ;
    @Value("${KEYCLOACK_USER:emcare_admin}")
    public void setNameStatic(String KEYCLOACK_USER){
        KeyCloakConfig.USER_NAME = KEYCLOACK_USER;
    }  

    public static  String PASSWORD;
    @Value("${KEYCLOAK_USER_PASSWORD:argusadmin}")
    public void setPassStatic(String KEYCLOAK_USER_PASSWORD){
        KeyCloakConfig.PASSWORD = KEYCLOAK_USER_PASSWORD;
    }    

    public static  String MASTER_USER_ID ;
    @Value("${KEYCLOAK_ADMIN_USER:emcare@gmail.com}")
    public void setMasterStatic(String KEYCLOAK_ADMIN_USER){
        KeyCloakConfig.MASTER_USER_ID = KEYCLOAK_ADMIN_USER;
    }    

    public static  String MASTER_USER_PASSWORD;
    @Value("${KEYCLOAK_ADMIN_PASSWORD:argusadmin}")
    public void setMasterPassStatic(String KEYCLOAK_ADMIN_PASSWORD){
        KeyCloakConfig.MASTER_USER_PASSWORD = KEYCLOAK_ADMIN_PASSWORD;
    }    

    public Keycloak getInstance() {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        keycloak = KeycloakBuilder.builder()
                .serverUrl(KeyCloakConfig.SERVER_URL)
                .realm(KeyCloakConfig.REALM)
                .grantType(OAuth2Constants.PASSWORD)
                .username(KeyCloakConfig.KEYCLOAK_ADMIN_USER)
                .password(KeyCloakConfig.KEYCLOAK_ADMIN_PASSWORD)
                .clientId(KeyCloakConfig.CLIENT_ID)
                .authorization(context.getTokenString())
                .clientSecret(KeyCloakConfig.CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return keycloak;
    }

    public Keycloak getInstanceByAuth() {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return KeycloakBuilder.builder()
                .serverUrl(KeyCloakConfig.SERVER_URL)
                .realm(KeyCloakConfig.REALM)
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }

    public static String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", KeyCloakConfig.MASTER_USER_ID);
        map.add("password", KeyCloakConfig.MASTER_USER_PASSWORD);
        map.add("grant_type", "password");
        map.add("client_id", KeyCloakConfig.CLIENT_ID);
        map.add("client_secret", KeyCloakConfig.CLIENT_SECRET);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        String url = KeyCloakConfig.SERVER_URL + "/realms/" + KeyCloakConfig.REALM + "/protocol/openid-connect/token";
        String token = null;
        AccessTokenForUser accessToken = restTemplate.postForObject(url, entity, AccessTokenForUser.class);
        if (accessToken != null) {
            token = accessToken.getAccess_token();
        }
        return token;
    }

    public Keycloak getInsideInstance() {
        String token = getAccessToken();
        return KeycloakBuilder.builder()
                .serverUrl(KeyCloakConfig.SERVER_URL)
                .realm(KeyCloakConfig.REALM)
                .authorization(token)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }
}
