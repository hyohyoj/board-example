package hyo.boardexample.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()   //cors 방지
                .csrf().disable()   //csrf 방지
                .formLogin().disable()  //기본 로그인 페이지 없애기
                .headers().frameOptions().disable()
                .and()
                .headers()
                .xssProtection();    // XSS 처리
        
        http
                .logout()
                .logoutSuccessUrl("/")  //로그아웃 시 리다이렉트 할 페이지
                .invalidateHttpSession(true)    //세션 삭제
                .deleteCookies("JSESSIONID");   //쿠키 삭제

//        http
//                .antMatcher("/admin/**")
//                .logout()
//                .logoutSuccessUrl("/admin")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
