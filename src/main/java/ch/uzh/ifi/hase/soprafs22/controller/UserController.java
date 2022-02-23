package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    /**
     * The new code for the user controller
     * -----------------------------------------------------------------------------------------------------------
     */


    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable long id) {
        userService.existsById(id); //checks if the user actually exists by ID.
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUserById(id));
    }

    @GetMapping("/users_name/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable String username) {
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUserByUsername(username));
    }

    // Server: UserService
    // Client: Login.js
    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable long id){
        userService.existsById(id); //checks if the user actually exists by ID.
        userService.updateStatus(id);
    }

    // Server: UserService
    // Client: Edit.js - changeUsername
    // updates username
    @PutMapping("/users/{id}/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUsername(@PathVariable long id, @RequestBody String username){
        userService.updateUsername(id, username);
    }

    // Server: UserService
    // Client: Edit.js - changeBirthday
    // updates Birthday
    @PutMapping("/users/{id}/birthday")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBirthday(@PathVariable long id, @RequestBody String birthday){
        userService.updateBirthday(id, birthday);
    }

    //the login function, conflict throws 409
    @PutMapping("/users_name/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO login(@RequestBody UserPostDTO userPostDTO, @PathVariable String username) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong username");
        }
        else if (user.getPassword().equals(userInput.getPassword())) {
            return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "wrong password");
        }
    }
}
