package com.filecompression.service;

import com.filecompression.model.User;
import com.filecompression.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    List noRoles = new ArrayList<SimpleGrantedAuthority>();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByConfirmationToken(String confirmationToken) {
        return userRepository.findByConfirmationToken(confirmationToken);
    }

    public User findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public User findByUsernameAndEnabled(String userName, boolean isEnabled) {
        return userRepository.findByUsernameAndEnabled(userName, isEnabled);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndEnabled(username, true);
        if (user == null) {
            throw new BadCredentialsException("Username not found.");
        } else {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_NO_ROLES");
            noRoles.add(simpleGrantedAuthority);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), noRoles);
        }
    }

}
