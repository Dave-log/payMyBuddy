package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.dto.RegisterDTO;
import com.payMyBuddy.payMyBuddy.service.AuthService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {

        com.payMyBuddy.payMyBuddy.model.User user = userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>("User is not authenticated or does not exist", HttpStatus.FORBIDDEN);
        } else {
            // TODO : renvoyer vers la page Accueil
            return new ResponseEntity<>("Welcome " + user.getFirstname() + " " + user.getLastname() + " !", HttpStatus.OK);
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {

        if (authService.existsByEmail(registerDTO.email())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        authService.register(registerDTO);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }

//    // TODO : SUPPRIMER TOUT CE QUI SUIT
//    @GetMapping("/")
//    public String getUserInfo(Principal user, @AuthenticationPrincipal OidcUser oidcUser) {
//        StringBuilder userInfos = new StringBuilder();
//        if(user instanceof UsernamePasswordAuthenticationToken) {
//            userInfos.append(getUsernamePasswordLoginInfo(user));
//        } else if(user instanceof OAuth2AuthenticationToken) {
//            userInfos.append(getOAuth2LoginInfo(user, oidcUser));
//        }
//        return userInfos.toString();
//    }
//
//    private StringBuffer getUsernamePasswordLoginInfo(Principal user) {
//        StringBuffer usernameInfo = new StringBuffer();
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;
//        if(token.isAuthenticated()) {
//            User u = (User) token.getPrincipal();
//            usernameInfo.append("Welcome, ").append(u.getUsername());
//        } else {
//            usernameInfo.append("NA");
//        }
//        return usernameInfo;
//    }
//
//    private StringBuffer getOAuth2LoginInfo(Principal user, OidcUser oidcUser) {
//        StringBuffer protectedInfo = new StringBuffer();
//        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) user;
//        OAuth2AuthorizedClient authClient = authorizedClientService.loadAuthorizedClient(
//                token.getAuthorizedClientRegistrationId(),
//                token.getName());
//        if(token.isAuthenticated()) {
//            Map<String, Object> userAttributes = token.getPrincipal().getAttributes();
//            String userToken = authClient.getAccessToken().getTokenValue();
//            protectedInfo.append("Welcome, ").append(userAttributes.get("name")).append("<br><br>");
//            protectedInfo.append("email : ").append(userAttributes.get("email")).append("<br><br>");
//            protectedInfo.append("Access token : ").append(userToken);
//
//            OidcIdToken idToken = oidcUser.getIdToken();
//            if(idToken != null) {
//                protectedInfo.append("idToken value : ").append(idToken.getTokenValue()).append("<br>");
//                protectedInfo.append("Token mapped values <br>");
//                Map<String, Object> claims = idToken.getClaims();
//                for(String key : claims.keySet()) {
//                    protectedInfo.append(" ").append(key).append(" : ").append(claims.get(key)).append("<br>");
//                }
//            }
//        } else {
//            protectedInfo.append("NA");
//        }
//        return protectedInfo;
//    }
}
