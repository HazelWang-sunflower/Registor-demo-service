package com.koi.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koi.api.entity.RestBean;
import com.koi.api.entity.dto.Account;
import com.koi.api.entity.response.AuthorizeVO;
import com.koi.api.filter.CustomAuthenticationFilter;
import com.koi.api.filter.JwtAuthorizeFilter;
import com.koi.api.service.AccountService;
import com.koi.api.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils jwtUtils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    AccountService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.
                authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                ).formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess)
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDenied)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        response.setContentType("application/json; charset=utf-8");
//        response.setCharacterEncoding("utf-8");
        // get user details

        User user = (User) authentication.getPrincipal();
        // #1 fetch DB to get username role etc
        // #2 threadlocal to store the user detail
        //example #1

        //new try start
        Object principal = authentication.getPrincipal();
        String username = new ObjectMapper().writeValueAsString(principal);
        // new try end

        Account account = service.findUserByNameOrEmail(user.getUsername());

        String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());

//        AuthorizeVO vo = new AuthorizeVO();
        AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, v->{
            v.setExpire(jwtUtils.expireTime());
            v.setToken(token);
        });
        BeanUtils.copyProperties(account, vo);
        vo.setExpire(jwtUtils.expireTime());
//        vo.setRole(account.getRole());
//        vo.setUsername(account.getUsername());
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    private void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(e.getMessage()).asJsonString());
    }

    private void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(jwtUtils.invalidJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }else {
            writer.write(RestBean.failure(400, "login out failed").asJsonString());
        }
    }

    private void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(authException.getMessage()).asJsonString());

    }
    // login but no access
    public void onAccessDenied(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

}
