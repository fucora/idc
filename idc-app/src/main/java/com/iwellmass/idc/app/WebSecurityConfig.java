package com.iwellmass.idc.app;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void  configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/task/*/info",
                        "/job/complete",
                        "/job/progress",
                        "/job/start",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/favicon.ico",
                        "/webjars/**",
                        "/static/**,*.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "*.json"
                );
    }
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http.csrf().disable();
//
//        http.authorizeRequests().antMatchers("/").permitAll()
//                .antMatchers(
//                "/task/*/info",
//                        "/idc-job/**",
//                        "/css/**",
//                        "/images/**",
//                        "/js/**",
//                        "/favicon.ico",
//                        "/webjars/**",
//                        "/static/**,*.html",
//                        "/swagger-resources/**",
//                        "/v2/api-docs",
//                        "*.json"
//                      ).permitAll()
//                .anyRequest().authenticated().and()
//                .formLogin().and().httpBasic();
//
//    }

}
