package com.hart.mfa.service.user;

import com.hart.mfa.dto.UserDto;
import com.hart.mfa.exception.CustomException;
import com.hart.mfa.model.User;
import com.hart.mfa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User createUser(User user) {
        try {
            //Check if email already exist
            Boolean emailAlreadyExist = userRepository.existsByEmail(user.getEmail());
            
            if (emailAlreadyExist) {
                throw new CustomException("Email: " + user.getEmail() + " already exist");
            }

            //If not register new user
            User newUser =  new User(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    new BCryptPasswordEncoder().encode(user.getPassword())
            );
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    @Override
    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
