package com.iyyxx.token;

import com.iyyxx.token.domain.User;
import com.iyyxx.token.mapper.UserMapper;
import com.iyyxx.token.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@SpringBootTest
class TokenApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testUserMapper() {
        List<User> users = userMapper.selectList(null);
        log.info("{}", users);
    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testBCryptPasswordEncoder() {
        String encode1 = passwordEncoder.encode("123456");
        String encode2 = passwordEncoder.encode("123456");
        log.info(encode1);
        log.info(encode2);

        boolean matches = passwordEncoder.matches("1234567",
                "$2a$10$U/Ddo7N4kjzi8leVux4HQOufJ8df6rpAnd0NkNLwPz4iQZMlpYD1y");
        log.info("{}",matches);
    }

    @Test
    void testJWT() {

        String token = "123123";
        String jwt = JwtUtil.createJWT(token);
        System.out.println(jwt);
        try {
            String subject = JwtUtil.parseJWT(jwt).getSubject();
            System.out.println(subject);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //
    @Test
    void testParseJWT() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlZWY2YjYzZDMyMDg0MzMyYTVhYjMwOWQxOGM2NmIwMCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY2NjA1NzkyMiwiZXhwIjoxNjY2MDYxNTIyfQ.2F-CJNF7XZSlQh6YDcxhBwvTusT6rqEZmx4NsrzpK9Y";
        try {
            String subject = JwtUtil.parseJWT(jwt).getSubject();
            System.out.println(subject);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
