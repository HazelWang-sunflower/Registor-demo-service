package com.koi.api.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.koi.api.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils jwtUtils;

    /**
     * verify the request header token
     * then wrap it username password token,
     * store the authorization to the global SecurityContextHolder fot the future use.
     * Store the user id to the request
     * But it doesn't verify the token whether is correct, just verify token is using sme Algorithm.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        DecodedJWT decodedJWT = jwtUtils.resolveJwt(authorization);
        if(decodedJWT != null) {
            UserDetails userDetails = jwtUtils.toUser(decodedJWT);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            request.setAttribute("id", jwtUtils.toId(decodedJWT));
        }
        filterChain.doFilter(request, response);
    }
}
