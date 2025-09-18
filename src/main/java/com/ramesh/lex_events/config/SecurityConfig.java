package com.ramesh.lex_events.config;

import com.ramesh.lex_events.models.AppRole;
import com.ramesh.lex_events.models.Role;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.RoleRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.security.jwt.AuthEntryPointJwt;
import com.ramesh.lex_events.security.jwt.AuthTokenFilter;
import com.ramesh.lex_events.security.jwt.JwtAccessDeniedHandler;
import com.ramesh.lex_events.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtUtils jwtUtils;


    public SecurityConfig(UserDetailsService userDetailsService, AuthEntryPointJwt unauthorizedHandler, JwtAccessDeniedHandler accessDeniedHandler, JwtUtils jwtUtils) {
        logger.info("Starting constructor in SecurityConfig");
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws  Exception{
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception{
        logger.info("Starting security filterchanin");
        return  http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler).accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/events/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/email/send-otp", "/api/email/verify-otp").hasRole("USER")
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                                .requestMatchers("/api/user/contact-preference").authenticated()
                                .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                //.httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Transactional
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      @Value("${app.admin.username}") String adminUsername,
                                      @Value("${app.admin.password}") String adminPassword,
                                      @Value("${app.admin.email}") String adminEmail,
                                      @Value("${app.admin.phone}") String adminPhone,
                                      @Value("${app.user.username}") String userUsername,
                                      @Value("${app.user.password}") String userPassword,
                                      @Value("${app.user.email}") String userEmail,
                                      @Value("${app.user.phone}") String userPhone

                                      ){
        logger.info("Inside command line runner");
        return args -> {


            //retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);

                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);

                    });
            Set<Role> userRoles = Set.of(userRole);
            Set<Role>adminRoles = Set.of(userRole, adminRole);

            //create user if not exist
            if(!userRepository.existsByUserNameIgnoreCase(userUsername.trim())){
                User newUser = new User(userUsername.trim(), userEmail.trim(),userPhone.trim(), passwordEncoder.encode(userPassword.trim()));
                newUser = userRepository.saveAndFlush(newUser);
                newUser.setRoles(userRoles);
                userRepository.save(newUser);
            }

            if(!userRepository.existsByUserNameIgnoreCase(adminUsername.trim())){
                User newAdmin = new User(adminUsername.trim(), adminEmail.trim(), adminPhone.trim(), passwordEncoder.encode(adminPassword.trim()));
                newAdmin = userRepository.saveAndFlush(newAdmin);
                newAdmin.setRoles(adminRoles);
                userRepository.save(newAdmin);
            }


            System.out.println("Default users and roles initialized successfully.");

            //update roles for users
            userRepository.findByUserNameIgnoreCase(userUsername).ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserNameIgnoreCase(adminUsername).ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });



        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
