package com.back.mozu.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 모든 문을 엽니다.
        return http.build();
    }

//    @Bean
//    @Order(2)
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/oauth2/**",
//                                "/api/v1/admin/auth/**"
//                        ).permitAll()
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(auth ->
//                                auth.baseUri("/api/v1/auth/oauth2/authorization"))
//                        .redirectionEndpoint(redir ->
//                                redir.baseUri("/api/v1/auth/oauth2/callback/*"))
//                        .userInfoEndpoint(userInfo ->
//                                userInfo.userService(customOAuth2UserService))
//                        .successHandler(oAuth2SuccessHandler)
//                )
//                .addFilterBefore(jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}