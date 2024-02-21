package com.sparta.team2newsfeed.service;

import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) //테스트메소드 실행전 캐시랑 비우고 깨끗한 상태에서 테스트
@ActiveProfiles("test")
class UserDetailsServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserDetailsService userDetailsService;
    User user;
    String username;

    @BeforeEach
    public void setup() {
        //사용하지 않는 메서드에도 추가되니 따로 data패키지 생성해서 분리하는게 좋음
        user = new User("dosal", "박승현", "123456789", "psh@g.g", "한마디");
        username = user.getUsername();
    }

    @Test
    @DisplayName("UserDetails 가져오기 - 성공")
    void test1(){
        //given
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        UserDetails userDetails = userDetailsService.getUserDetails(username);
        //then
        assertEquals(userDetails.getUsername(),user.getUsername());
        assertEquals(userDetails.getPassword(),user.getPassword());
    }

    @Test
    @DisplayName("UserDetails 가져오기 - 실패")
    void test2(){
        //given
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        //when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                        userDetailsService.getUserDetails(username)
        );
        //then
        assertEquals("Not Found" + username, exception.getMessage());
    }
}