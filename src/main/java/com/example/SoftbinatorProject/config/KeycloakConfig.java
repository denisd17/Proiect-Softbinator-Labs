package com.example.SoftbinatorProject.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@Import(KeycloakSpringBootConfigResolver.class)
class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        KeycloakAuthenticationProvider keycloakAuthenticationProvider
                = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

    //TODO: Config route permissions and remove role check from controllers
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http

                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/users/register-user", "/users/register-admin", "/login", "/refresh").permitAll()
                .antMatchers("/users/profile", "/users/receipts", "/users/add-funds", "/users/change-password").authenticated()
                .antMatchers(HttpMethod.GET, "/users/**").hasAnyRole("ADMIN")
                .antMatchers("/users/**").authenticated()
                .antMatchers("/posts/{postId}/comments").authenticated()
                .antMatchers(HttpMethod.GET, "/projects/{projectId}/posts/**").authenticated()
                .antMatchers("/projects/{projectId}/posts/**").hasAnyRole("ORG_ADMIN", "ORG_MODERATOR", "ADMIN")
                .antMatchers(HttpMethod.GET, "/organizations/{id}/projects/**").authenticated()
                .antMatchers("/projects/{projectId}/posts").hasAnyRole("ORG_ADMIN", "ORG_MODERATOR", "ADMIN")
                .antMatchers("/organizations/{id}/projects/{projectId}/donate", "/organizations/{id}/projects/{projectId}/purchase").authenticated()
                .antMatchers("/organizations/{id}/projects/**").hasAnyRole("ORG_ADMIN", "ORG_MODERATOR", "ADMIN")
                .antMatchers(HttpMethod.GET, "/organizations/{id}/moderators").hasAnyRole("ORG_ADMIN", "ADMIN")
                .antMatchers(HttpMethod.GET, "/organizations/**").authenticated()
                .antMatchers("/organizations/{id}/addModerator").hasAnyRole("ORG_ADMIN", "ADMIN")
                .antMatchers("/organizations/{id}/removeModerator").hasAnyRole("ORG_ADMIN", "ADMIN")
                .antMatchers(HttpMethod.POST, "/organizations").authenticated()
                .antMatchers("/organizations/{id}").hasAnyRole("ORG_ADMIN", "ADMIN")
                .anyRequest().permitAll();
    }
}
