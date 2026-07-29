package com.platform.iam.security.service;

import com.platform.iam.entity.Users;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetail implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    protected CustomUserDetail() {
    }

    /**
     * Constructs a new instance of {@link CustomUserDetail} based on the provided {@link Users}.
     *
     * @param user The {@link Users} from which to extract user details.
     *             <p>
     *             The constructor initializes the following fields:
     *             - {@code password}: The password of the user extracted from the provided {@link Users}.
     *             - {@code username}: The username of the user extracted from the provided {@link Users}.
     *             - {@code authorities}: A collection of granted authorities derived from the user's role's permissions.
     *             Each permission is transformed into a {@link SimpleGrantedAuthority} and collected into a list.
     */
    public CustomUserDetail(Users user) {
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.authorities = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                .distinct()
                .collect(Collectors.toList());
    }


    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return this.username;
    }

}
