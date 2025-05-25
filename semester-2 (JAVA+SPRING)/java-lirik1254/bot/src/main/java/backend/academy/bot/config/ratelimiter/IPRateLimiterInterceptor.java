package backend.academy.bot.config.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IPRateLimiterInterceptor implements HandlerInterceptor {
    private final IPRateLimiterService limiterService;

    public IPRateLimiterInterceptor(IPRateLimiterService limiterService) {
        this.limiterService = limiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String clientIP = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        String key = clientIP + ":" + endpoint;

        RateLimiter limiter = limiterService.getLimiter(key);
        Supplier<Boolean> restrictedCall = RateLimiter.decorateSupplier(limiter, () -> true);
        try {
            if (restrictedCall.get()) {
                return true;
            }
            return true;
        } catch (RequestNotPermitted ex) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return false;
        }
    }
}
