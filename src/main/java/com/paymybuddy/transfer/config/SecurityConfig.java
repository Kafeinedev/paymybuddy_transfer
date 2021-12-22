package com.paymybuddy.transfer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Handle the configuration of the security of the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Lazy // Cannot do unit test without this annotation. Cause circular dependency.
	private UserDetailsService userDetailsService;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**", "/images/**", "/createuser").permitAll().antMatchers("/**")
				.hasRole("USER").and().formLogin().loginPage("/login").defaultSuccessUrl("/mytransactions").permitAll()
				.and().rememberMe().userDetailsService(this.userDetailsService()).and().logout().logoutUrl("/logout");
	}

	/**
	 * Password encoder.
	 *
	 * @return the password encoder used by the framework.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This allow the configuration of the authenticationManager, to use given
	 * userDetailsService. This method is to be handled by the framework.
	 *
	 * @param AuthenticationManagerBuilder
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
}
