package fete.be.global.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        int status = httpResponse.getStatus();

        // 응답 시간이 500ms를 초과하는 경우 모니터링 대상
        if (duration > 500) {
            log.warn("[SLOW API] {} {} - {} ({}ms) at {}",
                    method, uri, status, duration,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 일반 API 로깅 - 현재는 500ms 초과만 모니터링
//        log.info("[API] {} {} - {} ({}ms)", method, uri, status, duration);
//        log.info("Time: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
