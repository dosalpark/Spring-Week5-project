package com.sparta.team2newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team2newsfeed.dto.CommentRequestDto;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.imp.UserDetailsImpl;
import com.sparta.team2newsfeed.service.BoardService;
import com.sparta.team2newsfeed.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = CommentController.class,
        excludeFilters = { //WebMvcConfigurer 클래스와 동일한 타입의 필터를 찾아서 제외 ≒ Security 해제
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)})
@ActiveProfiles("test")
class CommentControllerTest {
    MockMvc mockMvc; //http(get,post,put,delete) 요청 생성

    @Autowired
    WebApplicationContext webApplicationContext; //DispatcherServlet -> 핸들러 전달시 필요한 service,repository등의 빈들을 보관

    @Autowired
    ObjectMapper objectMapper; //Object <-> json 변환

    @MockBean
    CommentService commentService;
    @MockBean
    BoardService boardService;

    @BeforeEach
    void mockMVCSetup() {
        User mockUser = mock(User.class); //mockUser 생성
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class); //mockUserDetails 생성
        given(mockUserDetails.getUser()).willReturn(mockUser); //mockUserDetails의 User를 찾으면 처음 생성한 mockUser를 반환

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null)); //mockUserDetails를 가지고 Authentication을 생성

        //mockMvc를 생성함(SpringSecurity를 적용하지 않음, Security가 비활성화기 때문에 @WithMockUser/@WithAnonymousUser 어노테이션 사용안해도됨
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("코멘트 등록 - 성공")
    void test1() throws Exception {
        //given
        String comment = "댓글테스트";
        CommentRequestDto requestDto = new CommentRequestDto(comment);

        String postInfo = objectMapper.writeValueAsString(requestDto);

        //when-then
        mockMvc.perform(post("/api/board/1/comment")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("코멘트 수정 - 성공")
    void test2() throws Exception {
        //given
        Long commentId = 1L;
        String comment = "댓글 수정테스트";
        CommentRequestDto requestDto = new CommentRequestDto(comment);

        String postInfo = objectMapper.writeValueAsString(requestDto);

        //when-then
        mockMvc.perform(put("/api/board/1/comment/{commentId}", commentId)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("코멘트 삭제 - 성공")
    void test3() throws Exception {
        //given
        Long commentId = 1L;

        //when-then
        mockMvc.perform(delete("/api/board/1/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}