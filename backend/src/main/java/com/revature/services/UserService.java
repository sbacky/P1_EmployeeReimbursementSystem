package com.revature.services;

import java.util.List;
import java.util.Optional;

import com.revature.enums.Roles;
import com.revature.models.dtos.IncomingUserDto;
import com.revature.models.dtos.OutgoingUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.revature.models.User;
import com.revature.daos.UserDAO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Create a new User - All users can create a new User Account
     * @param userDTO object to create a new User
     * @return the created reimbursement
     * @throws IllegalArgumentException if no user is found at user ID
     */
    public OutgoingUserDTO createUser(IncomingUserDto userDTO) throws IllegalArgumentException {
       //Check the validity of all fields of the userDTO object
        if(userDTO.getFirstname().isBlank() || userDTO.getFirstname() == null){
            // LOGGER.error("Firstname cannot be empty");
            throw new IllegalArgumentException("Firstname cannot be empty");
        }

        if(userDTO.getLastname().isBlank() || userDTO.getLastname() == null){
            // LOGGER.error("Lastname cannot be empty");
            throw new IllegalArgumentException("Lastname cannot be empty");
        }

        if(userDTO.getUsername().isBlank() || userDTO.getUsername() == null){
            LOGGER.error("Username cannot be empty");
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if(userDTO.getPassword().isBlank() || userDTO.getPassword() == null){
            LOGGER.error("Password cannot be empty");
            throw new IllegalArgumentException("Password cannot be empty");
        }

        User user = new User(userDTO.getFirstname(), userDTO.getLastname(), userDTO.getUsername(), userDTO.getPassword());
        user.setRole("employee");
        try {
            userDAO.save(user);
            LOGGER.info("Successfully created user");
            return new OutgoingUserDTO(user.getUsername(), user.getFirstname(), user.getLastname(), user.getRole(), user.getUserId());
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Username already exists");
            throw new IllegalArgumentException("Username already exists");
        }
    }

    /**
     * Create a new User - Only managers can update a User
     * @param role and userId  - of the user to be updated.
     * @return the updated user
     * @throws IllegalArgumentException if no user is found at user ID
     */

    public OutgoingUserDTO updateUser(String role, int userid) {

        // Role still needs to be validated - we do allow update, only if the role  it is "pending or 'manager',
        if(role.isBlank() || role == null) {
            LOGGER.error("Role cannot be empty");
            throw new IllegalArgumentException("Role cannot be empty");
        }
        // normalize role to be lowercase and no additional whitespace before or after
        // format role to use role enum values
        role = Roles.formatRole(role.toLowerCase().trim());

        User user = userDAO.findById(userid).orElseThrow(() -> new IllegalArgumentException("No user found for ID: " + userid));
        user.setRole(role);
        userDAO.save(user);
        LOGGER.info("Successfully updated user");
        return new OutgoingUserDTO(user.getUsername(), user.getFirstname(),user.getLastname(), user.getRole(), user.getUserId());
    }

    public OutgoingUserDTO deleteUser (int userid) {
        User user = userDAO.findById(userid).orElseThrow(() -> new IllegalArgumentException("No user found for ID: " + userid));
        userDAO.deleteById(userid);
        LOGGER.info("Successfully deleted user");
        return new OutgoingUserDTO(user.getUsername(), user.getFirstname(), user.getLastname(), user.getRole(), user.getUserId());
    }

    public OutgoingUserDTO getUserById(int userid) {
        User user = userDAO.findById(userid).orElseThrow(() -> new IllegalArgumentException("No user found for ID: " + userid));
        LOGGER.info("Successfully retrieved user");
        return new OutgoingUserDTO(user.getUsername(), user.getFirstname(), user.getLastname(), user.getRole(), user.getUserId());
    }

    public Optional<User> loginUser(IncomingUserDto userDTO) throws IllegalArgumentException {
        // TODO: validity checks
        LOGGER.info("Logging in user: {}", userDTO.getUsername());
        return userDAO.findByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword());
    }

}