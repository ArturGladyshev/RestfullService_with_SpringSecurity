package com.config;

import com.config.jwt.AuthEntryPointJwt;
import com.config.jwt.AuthTokenFilter;
import com.config.jwt.JwtUtils;
import com.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
		@Autowired
		private UserDetailsServiceImp userDetailsService;

		@Autowired
		private AuthEntryPointJwt unauthorizedHandler;

		@Override
		public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
		{
				authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
				http.cors().and().csrf().disable()
					.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
					.authorizeRequests()
					.antMatchers("/api/auth/**").permitAll()
					.antMatchers("/api/test/**").permitAll()
					.anyRequest().authenticated();

				http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		}

		@Bean
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception
		{
				return super.authenticationManagerBean();
		}

		@Bean
		public PasswordEncoder passwordEncoder()
		{
				return new BCryptPasswordEncoder();
		}

		@Bean
		public UserDetailsService getUserDetailsService()
		{
				return new UserDetailsServiceImp();
		}

		@Bean
		public AuthTokenFilter authenticationJwtTokenFilter()
		{
				return new AuthTokenFilter();
		}

}
