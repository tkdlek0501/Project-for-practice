package com.productservice.demo.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsUtils;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import lombok.RequiredArgsConstructor;

@Configuration 
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final CustomAuthenticationProvider provider;
	private final CustomAuthenticationSuccessHandler successHandler;
	private final CustomAuthenticationFailureHandler failureHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable() //csrf 토큰 비활성화 (테스트 시 걸어두는 게 좋음) // 없으면 404 페이지...
			.authorizeRequests() // HttpServletRequest 을 사용하여 접근제한 설정 가능
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.antMatchers("/login/**").anonymous()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
				.anyRequest().permitAll() 
			.and()
			.formLogin() 
				.loginPage("/login") // login이 필요한 페이지 접근시 이동되는 url
				.loginProcessingUrl("/doLogin") 
				.successHandler(successHandler)
				.failureHandler(failureHandler)
			.and()
				.logout() // logout 활성화
				.logoutUrl("/doLogout") // logout을 실행할 url
				.logoutSuccessUrl("/"); // logout 시 이동되는 url
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}
	
	@Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
	
	// passwordEncoder는 순환참조 이슈 때문에 WebConfig에서 bean 등록
	
	// 시큐리티 설정에 굳이 포함 안해도 되는 매핑 경로 (css 등)
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		//web.ignoring().antMatchers("/static/**", "/assets/**");
	}
	
}
