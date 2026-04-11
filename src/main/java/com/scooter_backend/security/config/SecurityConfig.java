package com.scooter_backend.security.config;
import com.scooter_backend.security.jwt.JwtAuthFilter;
import com.scooter_backend.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http
                // ❌ csrf o‘chiriladi (JWT ishlatyapmiz)
                .csrf(csrf -> csrf.disable())

                // ❌ session yo‘q (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔐 authorization
                .authorizeHttpRequests(auth -> auth

                        // 🔓 PUBLIC endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 🔒 ADMIN endpoints
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")//.hasAuthority("ADMIN")

                        .requestMatchers("/api/operator/**").hasAnyAuthority("ADMIN", "OPERATOR")

                        .requestMatchers("/api/driver/orders/**").hasAnyAuthority("ADMIN", "DRIVER")

                        // 🔒 USER endpoints
                        .requestMatchers("/api/rides/**").hasAnyAuthority("ADMIN","DRIVER")//.hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ADMIN", "DRIVER")

                        .requestMatchers("/api/users/**").hasAuthority("ADMIN")

                        // 🔒 SCOOTER endpoints
                        .requestMatchers(HttpMethod.POST, "/api/scooters").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/scooters/*/status").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/scooters/nearest").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/scooters/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/scooters").permitAll()

                        // 🔒 LOCATION (real-time)
                        .requestMatchers("/api/location/**").permitAll()//.authenticated()

                        .requestMatchers("/api/driver/online").permitAll()
                        .requestMatchers("/api/driver/**").hasAnyAuthority("ADMIN","DRIVER")



                        // 🔒 qolgan hammasi
                        .anyRequest().authenticated()
                )

                // 🔑 auth provider
                .authenticationProvider(authenticationProvider())

                // 🔥 JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // 🔑 authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 🔐 password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 auth manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }




}