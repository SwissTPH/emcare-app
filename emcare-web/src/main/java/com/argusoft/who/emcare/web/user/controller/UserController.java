package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserUpdateDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getCurrentLoggedInUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUserResource(request).list());
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllRoles(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRoles(request).list());
    }

    /**
     * @param user
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/role/add")
    public ResponseEntity<Object> addRealmRole(@RequestBody RoleDto role) {
        userService.addRealmRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/role/{userId}")
    public ResponseEntity<Object> getUserRoleById(@PathVariable String userId) {
//        userService.addRealmRole(role, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/status/change")
    public ResponseEntity<Object> changeUserStatus(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUserStatus(userUpdateDto);
    }
}
