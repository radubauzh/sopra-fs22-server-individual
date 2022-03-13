package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(Boolean.FALSE);

        // Sets the creation Date when creating a new user
        LocalDate date = LocalDate.now();
        //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        newUser.setCreationDate(date);
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "This %s %s already taken. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        } // changed to conflict bc conflict throws 409
    }

    /**
     * Basically the new code for the user service :-)
     * -----------------------------------------------------------------------------------------------------------
     */

    // gets & returns the user via the findByUsername call in the userRepository
    public User getUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    // gets & returns the user via the findById call in the userRepository
    public User getUserById(long id) {
        return this.userRepository.findById(id);
    }


    // Server: UserController
    // gets the user by ID (User repo)
    // sets the status to Online if User is Offline or other way around
    public void updateStatus(long id) {
        User user = this.getUserById(id);
        if (user.getStatus() == Boolean.FALSE){
            user.setStatus(Boolean.TRUE);
        }
        else{
            user.setStatus(Boolean.FALSE);
        }
        userRepository.flush(); // used to make sure data is written, else it could be buffered
    }

    // Server: UserController
    // Client: Edit.js
    // updates Birthday
    public void updateBirthday(long id, LocalDate birthday) {
        User user = this.getUserById(id);
        user.setBirthday(birthday);
        // SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //user.setBirthday(formatter.format(birthday));

        userRepository.flush(); // used to make sure data is written, else it could be buffered
    }

    // Server: UserController
    // Client: Edit.js
    // updates username
    public void updateUsername(long id, String username) {
        User user = this.getUserById(id);

        String baseErrorMessage = "This %s %s already taken. Therefore, the user could not be created!";
        if (userRepository.existsUserByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
        }else

            user.setUsername(username);
        userRepository.flush(); // used to make sure data is written, else it could be buffered

    }

    public void existsById(long id){
        if(this.getUserById(id) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by ID Errorcode 404");
        }
    }
}
