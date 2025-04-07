package tradingSim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tradingSim.exception.*;
import tradingSim.model.User;
import tradingSim.service.UserManager;

import java.util.*;
import java.util.Collection;

@RestController
public class UserController {

    private UserManager userManager;

    @Autowired
    public UserController() {
        this.userManager = UserManager.getInstance();
    }

    @GetMapping("/user")
    public ResponseEntity<Collection<User>> allUsers() {
        Collection<User> users = userManager.getAllUsers();
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        try {
            User user = userManager.getUser(id);
            return ResponseEntity.status(HttpStatus.FOUND).body(user);
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (MissingUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<String> addUsers(@RequestBody Collection<User> users) {
        for (User user : users) {
            try {
                userManager.addUser(user.getUserId());
            } catch (DataValidationException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Users added successfully.");
    }


}
