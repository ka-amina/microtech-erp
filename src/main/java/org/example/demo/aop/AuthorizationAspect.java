package org.example.demo.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.demo.enums.UserRole;
import org.example.demo.exception.ForbiddenException;
import org.example.demo.exception.UnauthorizedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private UserRole getCurrentUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedException("No request context available");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String roleStr = (String) request.getSession(false).getAttribute("userRole");
        
        if (roleStr == null) {
            throw new UnauthorizedException("You must be logged in to access this resource");
        }
        
        return UserRole.valueOf(roleStr);
    }

    private boolean isAuthenticated() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        
        HttpServletRequest request = attributes.getRequest();
        return request.getSession(false) != null && 
               request.getSession(false).getAttribute("userId") != null;
    }


    @Before("@annotation(org.example.demo.aop.RequiresAdmin)")
    public void checkAdminAccess() {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to access this resource");
        }
        
        UserRole currentRole = getCurrentUserRole();
        if (currentRole != UserRole.ADMIN) {
            throw new ForbiddenException("Admin role required to perform this operation");
        }
    }

    @Before("@annotation(org.example.demo.aop.RequiresClient)")
    public void checkClientAccess() {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to access this resource");
        }
        
        UserRole currentRole = getCurrentUserRole();
        if (currentRole != UserRole.CLIENT) {
            throw new ForbiddenException("Client role required to perform this operation");
        }
    }

    @Before("@annotation(org.example.demo.aop.RequiresAuthenticated)")
    public void checkAuthenticated() {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to access this resource");
        }
    }
}
