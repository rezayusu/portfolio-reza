package dev.portfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${ADMIN_USERNAME:portfolio-admin}") String username,
            @Value("${ADMIN_PASSWORD:}") String password) {
        if (password == null || password.isBlank() || password.startsWith("CHANGE_THIS")) {
            throw new IllegalStateException(
                "Set a strong ADMIN_PASSWORD environment variable before starting the app.");
        }
        return new InMemoryUserDetailsManager(
            User.withUsername(username)
                .password(encoder.encode(password))
                .roles("ADMIN")
                .build()
        );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/login.css",
                    "/favicon.ico", "/error", "/api/projects", "/api/contact",
                    "/api/csrf", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/admin/preview").permitAll()
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            // Keep CSRF enabled for the public contact form and normal browser sessions.
            // Admin endpoints are protected by HTTP Basic + role authorization.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/admin/**"))
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        http.headers(headers -> headers
            .contentTypeOptions(Customizer.withDefaults())
            .frameOptions(frame -> frame.deny())
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; script-src 'self' https://cdn.tailwindcss.com; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.tailwindcss.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'"))
        );
        return http.build();
    }
}
