package com.hvs.kotlinspringplayground.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

// TODO: proper configurations for combination UI and for M2M. UI preferably based on roles
@Configuration
@EnableWebSecurity
class SecurityConfig {

//    @Bean
//    @Order(1)
//    fun m2mFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .securityMatcher(AntPathRequestMatcher("/api/v1/user/m2m/**"))
//            .securityMatcher(AntPathRequestMatcher("/api/v1/artist/m2m/**"))
//            .securityMatcher(AntPathRequestMatcher("/api/v1/release/m2m/**"))
//            .cors { cors -> cors.disable() }
//            .csrf { csrf -> csrf.disable() }
//            .sessionManagement { sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
//            .authorizeHttpRequests { authorize ->
//                authorize.anyRequest().authenticated()
//            }
//            .oauth2ResourceServer { resourceServer ->
//                resourceServer.jwt(Customizer.withDefaults())
//                }
//        return http.build()
//    }

//    @Bean
//    @Order(2)
//    fun uiFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .securityMatcher(AntPathRequestMatcher("/api/v1/**"))
//            .cors(Customizer.withDefaults())
//            .csrf { csrf -> csrf.ignoringRequestMatchers("/api/v1/**") }
//            .authorizeHttpRequests { authorize ->
////                authorize.requestMatchers(antMatcher(GET, "/api/v1/user/parameters")).hasAuthority("manage-account")
//                authorize.anyRequest().authenticated()
//            }
//            .sessionManagement { sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
//            .oauth2ResourceServer { resourceServer ->
//                resourceServer.jwt(Customizer.withDefaults())
//                }
//        return http.build()
//    }


    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    .requestMatchers("/api/v1/**").authenticated()
            }

            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }

        return http.build()
    }
}

