package com.hvs.kotlinspringplayground.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private val jwkSetUri: String = ""

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuerUri: String = ""

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()

        val issuerValidator = OAuth2TokenValidator<Jwt> { token ->
            val expected = issuerUri
            val actual   = token.issuer?.toString()
            when {
                actual == null -> {
                    OAuth2TokenValidatorResult.failure(
                        OAuth2Error(
                            "invalid_token",
                            "Missing 'iss' claim in JWT; expected '$expected'",
                            null
                        )
                    )
                }
                actual != expected -> {
                    OAuth2TokenValidatorResult.failure(
                        OAuth2Error(
                            "invalid_token",
                            "Bad 'iss' claim: expected '$expected' but was '$actual'",
                            "Double-check your Keycloak realm’s Frontend URL / proxy settings, and your Spring `issuer-uri`"
                        )
                    )
                }
                else -> OAuth2TokenValidatorResult.success()
            }
        }

        val audienceValidator = OAuth2TokenValidator<Jwt> { token ->
            val requiredAud = "music-release-notifier"
            if (token.audience.contains(requiredAud)) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error(
                        "invalid_token",
                        "Missing required audience: '$requiredAud'",
                        "Have your React app request the token for the correct client ID?"
                    )
                )
            }
        }

        val defaultValidator = JwtValidators.createDefault()
        val combined = DelegatingOAuth2TokenValidator(defaultValidator, issuerValidator, audienceValidator)
        decoder.setJwtValidator(combined)

        return decoder
    }

    @Bean
    fun uiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(AntPathRequestMatcher("/api/v1/**"))
            .cors(Customizer.withDefaults())
            .csrf { it.ignoringRequestMatchers("/api/v1/**") }
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .sessionManagement { s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2ResourceServer { rs ->
                // This will pick up your custom JwtDecoder() bean
                rs.jwt(Customizer.withDefaults())
            }

        return http.build()
    }

    companion object {
        val log = KotlinLogging.logger { }
    }
}
