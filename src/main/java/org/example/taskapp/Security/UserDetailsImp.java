package org.example.taskapp.Security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.taskapp.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDetailsImp implements UserDetails {
    private UUID id;
    private String username;
    private String email;
    private String password;

    public static UserDetailsImp build(User user){
        return new UserDetailsImp(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
