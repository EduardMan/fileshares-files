package tech.itparklessons.fileshares.files.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Data
public class User {
    private Long id;
    private String login;
    private String username;
    private String email;
    private Collection<GrantedAuthority> authorities = Collections.emptyList();
}
