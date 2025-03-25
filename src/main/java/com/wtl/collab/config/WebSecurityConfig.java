package com.wtl.collab.config;


import com.google.api.Http;
import com.wtl.collab.model.AppRole;
import com.wtl.collab.model.Role;
import com.wtl.collab.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    private JwtFilter jwtFilter;

    private UserDetailsService userDetailsService;

    private RoleRepository roleRepository;

    public WebSecurityConfig(JwtFilter jwtFilter,
                             UserDetailsService userDetailsService,
                             RoleRepository roleRepository
                             ){
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
                    return http
//                            .cors(cors -> cors.configurationSource(request -> {
//                                CorsConfiguration config = new CorsConfiguration();
//                                config.setAllowedOrigins(List.of("http://localhost:5173"));
//                                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//                                config.setAllowCredentials(true);
//                                config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Set-Cookie"));
//                                return config;
//                            }))
                            .cors(Customizer.withDefaults())
                            .csrf(customizer -> customizer.disable())
                            .formLogin(Customizer.withDefaults())
                            .httpBasic(Customizer.withDefaults())
                            .oauth2Client(Customizer.withDefaults())
                            .authorizeHttpRequests(request -> request
                                    .requestMatchers("/api/user/register" , "/api/user/login",
                                            "/api/user/create", "/api/user/is-logged-in" )
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated())
                            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                            .addFilterBefore(jwtFilter , UsernamePasswordAuthenticationFilter.class)
                            .build();
    }






    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository){
        return  args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.USER)
                    .orElseGet(()->{
                        Role newUserRole = new Role(AppRole.USER);
                        return roleRepository.save(newUserRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ADMIN)
                    .orElseGet(()->{
                        Role newAdminRole = new Role(AppRole.ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

        };
    }


}
