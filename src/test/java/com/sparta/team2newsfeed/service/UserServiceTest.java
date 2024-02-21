package com.sparta.team2newsfeed.service;

import com.sparta.team2newsfeed.dto.StatusResponseDto;
import com.sparta.team2newsfeed.dto.UserRequestDto;
import com.sparta.team2newsfeed.dto.UserUpdateRequestDto;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) //테스트메소드 실행전 캐시랑 비우고 깨끗한 상태에서 테스트
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserService userService;
    User user;

    @BeforeEach
    public void setup() {
        //사용하지 않는 메서드에도 추가되니 따로 data패키지 생성해서 분리하는게 좋음
        user = new User("dosal", "박승현", "123456789", "psh@g.g", "한마디");
    }


    @Test
    @DisplayName("회원가입 -정상")
    void test1() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", "박승현", "123456789", "p@1@1", "자기소개");
        given(passwordEncoder.encode(dto.getPassword())).willReturn("암호화된패스워드");
        given(userRepository.findByUsername(dto.getUsername())).willReturn(Optional.empty());
        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());
        User addUser = new User(dto.getUsername(), dto.getName(), "암호화된패스워드", dto.getEmail(), dto.getIntro());
        //when
        userService.userSignup(dto);
        //then
        assertEquals(addUser.getUsername(), dto.getUsername());
        assertEquals(addUser.getPassword(), passwordEncoder.encode(dto.getPassword()));
        assertEquals(addUser.getEmail(), dto.getEmail());
        assertEquals(addUser.getName(), dto.getName());
        assertEquals(addUser.getIntro(), dto.getIntro());
    }

    @Test
    @DisplayName("회원가입 - username 중복")
    void test2() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", "박승현", "123456789", "p@1@1", "자기소개");
        given(passwordEncoder.encode(dto.getPassword())).willReturn("암호화된패스워드");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userSignup(dto)
        );
        //then
        assertEquals("이미 존재하는 유저 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 - email 중복")
    void test3() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", "박승현", "123456789", "p@1@1", "자기소개");
        given(passwordEncoder.encode(dto.getPassword())).willReturn("암호화된패스워드");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userSignup(dto)
        );
        //then
        assertEquals("이미 가입된 email 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 - 성공")
    void test4() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", null, "123456789", null, null);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(dto.getPassword(), user.getPassword())).willReturn(true);
        //when
        userService.userLogin(dto);
        //then
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("로그인 - 사용자 없음")
    void test5() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", null, "123456789", null, null);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userLogin(dto)
        );
        //then
        assertEquals("등록된 사용자가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 - 패스워드 틀림")
    void test6() {
        //given
        UserRequestDto dto = new UserRequestDto("dosal", null, "123456789", null, null);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(dto.getPassword(), user.getPassword())).willReturn(false);
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userLogin(dto)
        );
        //then
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원수정 - 성공")
    void test7() {
        //given
        UserUpdateRequestDto dto = new UserUpdateRequestDto("dosal1234", "123456789", "psh_a@naver.com", "나만의설명");
        //repo에 저장이안되서 id값이 없음..
        given(userRepository.findById(null)).willReturn(Optional.of(user));
        given(passwordEncoder.encode(anyString())).willReturn("암호화된패스워드");
        //when
        userService.userUpdate(dto, user);
        //then
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getPassword(), passwordEncoder.encode(dto.getPassword()));
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getIntro(), dto.getIntro());
    }

    @Test
    @DisplayName("회원수정 - 유저명 중복")
    void test8() {
        //given
        UserUpdateRequestDto dto = new UserUpdateRequestDto("dosal", "123456789", "psh_a@naver.com", "나만의설명");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userUpdate(dto, user)
        );
        //then
        assertEquals("이미 존재하는 유저 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원수정 - email 중복")
    void test9() {
        //given
        UserUpdateRequestDto dto = new UserUpdateRequestDto("dosal1234", "123456789", "psh@g.g", "나만의설명");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.userUpdate(dto, user)
        );
        //then
        assertEquals("이미 가입된 Email 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원수정 - 로그인유저정보 사라짐")
    void test10() {
        //given
        UserUpdateRequestDto dto = new UserUpdateRequestDto("dosal1234", "123456789", "psh_a@naver.com", "나만의설명");
        //repo에 저장이안되서 id값이 없음..
        given(userRepository.findById(null)).willReturn(Optional.empty());
        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.userUpdate(dto, user)
        );
        //then
        assertEquals("로그인 유저 정보가 없음", exception.getMessage());
    }

    @Test
    @DisplayName("회원삭제 - 성공")
    void test11() {
        //given
        Long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        //when
        ResponseEntity<?> response = userService.userDelete(userId);
        //then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("유저 삭제 성공", ((StatusResponseDto) response.getBody()).getMsg());
    }

    @Test
    @DisplayName("회원삭제 - 실패")
    void test12() {
        //given
        Long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        //when
        ResponseEntity<?> response = userService.userDelete(userId);
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 유저가 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }
}