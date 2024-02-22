package com.sparta.team2newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team2newsfeed.dto.AddBoardRequestDto;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.jwt.JwtUtil;
import com.sparta.team2newsfeed.service.BoardService;
import com.sparta.team2newsfeed.service.CommentService;
import com.sparta.team2newsfeed.service.LikesService;
import com.sparta.team2newsfeed.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class BoardControllerTest2 {
    @Autowired
    MockMvc mockMvc; //http(get,post,put,delete) 요청 생성
    @Autowired
    ObjectMapper objectMapper; //Object <-> json 변환

    @MockBean
    BoardService boardService;
    @MockBean
    CommentService commentService;
    @MockBean
    LikesService likesService;
    @MockBean
    UserService userService;
    @MockBean
    JwtUtil jwtUtil;

    @Test
    @WithMockUser
    @DisplayName("전체 게시글 조회 - 성공")
    void test1() throws Exception {
        mockMvc.perform(get("/api/board"))// http요청 + url
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("단일 게시글 조회 - 성공")
    void test2() throws Exception {
        //given
        Long boardId = 1L;
        //when-then
        mockMvc.perform(get("/api/board/{boardId}", boardId)) //http요청 + url
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("카테고리별 게시글 조회 - 성공")
    void test3() throws Exception {
        //given
        String category = "KOREAN";
        //when-then
        mockMvc.perform(get("/api/board/category/{categoryName}", category)) //http요청 + url
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("난이도별 게시글 조회 - 성공")
    void test4() throws Exception {
        //given
        int cookLevel = 3;
        //when-then
        mockMvc.perform(get("/api/board/cooklevel/{cookLevel}", cookLevel)) //http요청 + url
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 작성 - 성공")
    void test5() throws Exception {
        //given
        AddBoardRequestDto requestDto = new AddBoardRequestDto(
                "요리제목",
                "요리내용",
                "KOREAN",
                5,
                new User()
        );
        String postInfo = objectMapper.writeValueAsString(requestDto);

        //when-then
        mockMvc.perform(post("/api/boardmake")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 수정 - 성공")
    void test6() throws Exception {
        //given
        Long boardId = 1L;
        AddBoardRequestDto requestDto = new AddBoardRequestDto(
                "요리제목",
                "요리내용",
                "WESTERN",
                3,
                new User()
        );
        String postInfo = objectMapper.writeValueAsString(requestDto);

        //when-then
        mockMvc.perform(put("/api/boardmake/{boardId}", boardId)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 삭제 - 성공")
    void test7() throws Exception {
        //given
        Long boardId = 1L;

        //when-then
        mockMvc.perform(delete("/api/boardmake/{boardId}", boardId)
                        .with(csrf()))
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }
}