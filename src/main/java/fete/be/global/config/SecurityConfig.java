package fete.be.global.config;


import fete.be.global.jwt.JwtAuthenticationEntryPoint;
import fete.be.global.jwt.JwtAuthenticationFilter;
import fete.be.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     *  H2 Security 통과하도록 하는 코드
     */
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled",havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .requestMatchers("/api/emails/**").permitAll()
                .requestMatchers("/api/members/signup", "/api/members/login",
                        "/api/members/kakao/signup", "/api/members/kakao/login",
                        "/api/members/apple/signup", "/api/members/apple/login",
                        "/api/members/admin", "/api/members/find-id", "/api/members/find-password", "/api/members/check-token").permitAll()
                .requestMatchers("/api/banners/**", "/api/categories/**").permitAll()
                .requestMatchers("/api/popups").permitAll()
                .requestMatchers("/api/posters", "/api/posters/{posterId}", "/api/posters/search").permitAll()
                .requestMatchers("/api/admins/login").permitAll()
                .requestMatchers("/api/admins/**").hasRole("ADMIN")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
