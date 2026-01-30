package com.huakang.admin.filter;

import com.huakang.service.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 从请求头中提取JWT Token并验证，设置Spring Security认证上下文
 *
 * @author huakang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                // 验证Token是否有效
                if (jwtUtils.validateToken(token)) {
                    // 从Token中获取用户信息
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    String username = jwtUtils.getUsernameFromToken(token);
                    String role = jwtUtils.getRoleFromToken(token);
                    
                    if (username != null && role != null) {
                        // 创建认证对象
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(authority)
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置到Spring Security上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("JWT认证成功: username={}, role={}, userId={}", username, role, userId);
                    } else {
                        log.warn("JWT Token中缺少用户信息: username={}, role={}, userId={}", username, role, userId);
                    }
                } else {
                    // Token验证失败，尝试解析获取更详细的错误信息
                    try {
                        io.jsonwebtoken.Claims claims = jwtUtils.getClaimsFromToken(token);
                        if (claims.getExpiration().before(new java.util.Date())) {
                            log.warn("JWT Token已过期: 过期时间={}, 当前时间={}", 
                                    claims.getExpiration(), new java.util.Date());
                        } else {
                            log.warn("JWT Token验证失败: Token格式可能不正确");
                        }
                    } catch (Exception e) {
                        log.warn("JWT Token解析失败: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("JWT Token已过期: 过期时间={}, 当前时间={}", 
                        e.getClaims().getExpiration(), new java.util.Date());
                SecurityContextHolder.clearContext();
            } catch (io.jsonwebtoken.security.SignatureException e) {
                log.warn("JWT Token签名无效: 请检查JWT密钥配置是否正确");
                SecurityContextHolder.clearContext();
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                log.warn("JWT Token格式错误: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.warn("JWT Token验证失败: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                // Token无效，清除认证上下文，让Spring Security处理
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("请求中未包含JWT Token，请求路径: {}", request.getRequestURI());
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
