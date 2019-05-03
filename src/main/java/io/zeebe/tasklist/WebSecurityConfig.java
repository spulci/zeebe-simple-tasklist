package io.zeebe.tasklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserService userService;

  @Autowired
  private RestServicesBasicAuthenticationEntryPoint authenticationEntryPoint;
 

  /*
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**", "/img/**", "/js/**", "/fonts/**", "/favicon.ico")
        .permitAll()
        .antMatchers("/login", "/login-error")
        .permitAll()
        .antMatchers("/views/users", "/api/users", "/views/groups", "/api/groups")
        .hasRole(Roles.ADMIN.name())
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/", true)
        .failureUrl("/login-error")
        .permitAll()
        .and()
        .logout()
        .permitAll();
  }
  */

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.
        csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/api/users/**", "/api/groups/**").hasRole(Roles.ADMIN.name())
        .antMatchers("/api/tasks/**").authenticated()
        .and()
        .httpBasic()
        .authenticationEntryPoint(authenticationEntryPoint);
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    return userService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider());
  }
}
