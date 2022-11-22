package voting_system;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().authorizeRequests().anyRequest().authenticated().and().formLogin();   //todo make this look less stolen
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().anyRequest();    //todo tidy this up
    }
}
