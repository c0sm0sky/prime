package pl.demo.prime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@Profile("wildfly | docker | springboot")
public class WebConfig {
    @Bean
    OncePerRequestFilter loginFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain chain
            ) throws ServletException, IOException {
                String path = request.getRequestURI().substring(request.getContextPath().length());
                if (path.equals("/")) {
                    request.getRequestDispatcher("/index.xhtml").forward(request, response);
                    return;
                }
                if (path.equals("/dashboard.xhtml")) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }
                if (path.equals("/login.xhtml")) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }
                boolean publicPath = path.equals("/index.xhtml")
                        || path.startsWith("/resources/")
                        || path.startsWith("/jakarta.faces.resource/")
                        || path.startsWith("/javax.faces.resource/");
                if (!publicPath
                        && request.getSession(false) != null
                        && Boolean.TRUE.equals(request.getSession(false).getAttribute("loggedIn"))) {
                    chain.doFilter(request, response);
                    return;
                }
                if (!publicPath) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }
                chain.doFilter(request, response);
            }
        };
    }
}
