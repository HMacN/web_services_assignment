package voting_system;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static voting_system.controller.VotingController.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers
                (MEMBER_LOGIN_SUB_PATH,
                        MEMBER_LOGOUT_SUB_PATH,
                        MEMBER_VOTING_SUB_PATH,
                        MEMBER_WITHDRAW_VOTE_SUB_PATH,
                        MEMBER_VOTING_SUB_PATH + "/*",  // To handle path variables.
                        ADMIN_LOGIN_SUB_PATH,
                        ADMIN_LOGOUT_SUB_PATH,
                        ADMIN_TALLY_SUB_PATH,
                        ADMIN_OPEN_VOTING_SUB_PATH,
                        ADMIN_CLOSE_VOTING_SUB_PATH
                        );
    }
}
