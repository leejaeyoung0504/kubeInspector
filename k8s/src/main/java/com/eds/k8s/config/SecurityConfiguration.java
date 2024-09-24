/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eds.k8s.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) // CSRF 비활성화
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/login", "/static/**").permitAll()  // 로그인 페이지와 정적 리소스 접근 허용
	            .requestMatchers("/WEB-INF/views/login.jsp").permitAll() // JSP 경로에 대한 인증 허용
	            .requestMatchers("/samples/**").permitAll()
	            .anyRequest().authenticated()  // 나머지 경로는 인증 필요
	        )
	        .formLogin(form -> form
	            .loginPage("/login")  // 로그인 페이지 설정
	            .defaultSuccessUrl("/main", true)  // 로그인 성공 시 리디렉션할 기본 URL
	            .permitAll()  // 모든 사용자에게 로그인 페이지 접근 허용
	        )
	        .logout(logout -> logout
	            .logoutSuccessUrl("/login")  // 로그아웃 후 로그인 페이지로 이동
	            .invalidateHttpSession(true)
	            .permitAll()
	        );

	    return http.build();
	}
}
