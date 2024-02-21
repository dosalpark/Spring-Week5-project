package com.sparta.team2newsfeed.repository;

import com.sparta.team2newsfeed.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User user = User.builder().name("테스트").email("psh@naver.com").intro("자기소개").name("박승현").username("dosal").password("123456789").build();
        userRepository.save(user);

    }

    @Test
    @DisplayName("유저 이름 검색 - 성공")
    void test1() {
        //given
        String username = "dosal";
        //when
        Optional<User> searchUser = userRepository.findByUsername(username);
        //then
        assertNotNull(searchUser);
        assertEquals(username, searchUser.get().getUsername());
    }

    @Test
    @DisplayName("유저 이름 검색 - 없는 이용자 검색")
    void test2() {
        //given
        String username = "zzaaqq";
        //when
        Optional<User> searchUser = userRepository.findByUsername(username);
        //then
        assertTrue(searchUser.isEmpty());
    }

    @Test
    @DisplayName("유저 이메일 검색 - 성공")
    void test3() {
        //given
        String email = "psh@naver.com";
        //when
        Optional<User> searchUser = userRepository.findByEmail(email);
        //then
        assertNotNull(searchUser);
        assertEquals(email, searchUser.get().getEmail());
    }

    @Test
    @DisplayName("유저 이메일 검색 - 없는 이용자 검색")
    void test4() {
        //given
        String email = "1@1.1";
        //when
        Optional<User> searchUser = userRepository.findByEmail(email);
        //then
        assertTrue(searchUser.isEmpty());
    }
}