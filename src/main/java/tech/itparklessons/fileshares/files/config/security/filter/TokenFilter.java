package tech.itparklessons.fileshares.files.config.security.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.itparklessons.fileshares.files.model.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.stream.Collectors;

public class TokenFilter extends OncePerRequestFilter {

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        var token = request.getHeader("Authorization");
        if (token == null || !authenticationIsRequired()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            User user = fromToken(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return;
        }

        filterChain.doFilter(request, response);
    }

    User fromToken(String token) throws ParseException {
        //remove prefix Bearer
        token = token.substring(7);
        JWTClaimsSet jwtClaimsSet = JWTParser.parse(token).getJWTClaimsSet();
        User user = new User();

        user.setId(jwtClaimsSet.getLongClaim("userId"));
        user.setLogin(jwtClaimsSet.getSubject());
        user.setUsername(jwtClaimsSet.getStringClaim("username"));
        user.setEmail(jwtClaimsSet.getStringClaim("email"));
        user.setAuthorities(jwtClaimsSet.getStringListClaim("authorities").stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        return user;
    }

    private boolean authenticationIsRequired() {
        final var existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return false;
    }
}
