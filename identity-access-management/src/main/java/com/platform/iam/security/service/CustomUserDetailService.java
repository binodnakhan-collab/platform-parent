package com.platform.iam.security.service;

import com.platform.iam.entity.Users;
import com.platform.iam.repository.UserRepository;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user-specific data by username.
     *
     * @param username The username of the user to load data for.
     * @return The UserDetails object containing the user's information.
     * @throws UsernameNotFoundException If the user with the given username is not found.
     */
    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found : " + username));
        return new CustomUserDetail(user);
    }


}
