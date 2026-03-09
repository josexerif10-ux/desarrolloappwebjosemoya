package com.openwebinars.todo.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests ->
                requests.requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/", "/login", "/logout", "/auth/register",
                                "/auth/register/submit", "/h2-console/**",
                                "/img/**", "/css/**", "/error").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                    if (isAdmin) {
                        response.sendRedirect("/admin/");
                    } else {
                        response.sendRedirect("/task");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.requestCache(cache -> {
            HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
            requestCache.setMatchingRequestParameterName(null);
            cache.requestCache(requestCache);
        });

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll());

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        http.headers(headers -> headers.frameOptions(opts -> opts.disable()));

        return http.build();
    }
}