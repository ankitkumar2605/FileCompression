package com.filecompression.repository;

import com.filecompression.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    User findByConfirmationToken(String confirmationToken);
    User findByUsername(String userName);
    User findByUsernameAndEnabled(String userName,boolean isEnabled);

}