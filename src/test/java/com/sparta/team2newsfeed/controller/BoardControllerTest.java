package com.sparta.team2newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team2newsfeed.dto.AddBoardRequestDto;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.imp.UserDetailsImpl;
import com.sparta.team2newsfeed.jwt.JwtUtil;
import com.sparta.team2newsfeed.service.BoardService;
import com.sparta.team2newsfeed.service.CommentService;
import com.sparta.team2newsfeed.service.LikesService;
import com.sparta.team2newsfeed.service.UserService;
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

//JPA에서 제공하는 Auditing등 기능을 Mock객체로 대체
// (새로 class생성해서  @EnableJpaAuditing 따로 빼놔도됨)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = BoardController.class,
        excludeFilters = { //WebMvcConfigurer 클래스와 동일한 타입의 필터를 찾아서 제외 ≒ Security 해제
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class)})
@ActiveProfiles("test")
class BoardControllerTest {
    MockMvc mockMvc; //http(get,post,put,delete) 요청 생성

    @Autowired
    WebApplicationContext webApplicationContext; //DispatcherServlet -> 핸들러 전달시 필요한 service,repository등의 빈들을 보관

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

    @BeforeEach
        //http요청을 생성할 객체가 security에 영향을 받지 않도록 필터를 안거치게 설정
    void mockMVCSetup() {
        User mockUser = mock(User.class); //mockUser 생성
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class); //mockUserDetails 생성
        given(mockUserDetails.getUser()).willReturn(mockUser); //mockUserDetails의 User를 찾으면 처음 생성한 mockUser를 반환

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null)); //mockUserDetails를 가지고 Authentication을 생성

        //mockMvc를 생성함(SpringSecurity를 적용하지 않음, Security가 비활성화기 때문에 @WithMockUser/@WithAnonymousUser 어노테이션 사용안해도됨
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("전체 게시글 조회 - 성공")
    void test1() throws Exception {
        mockMvc.perform(get("/api/board"))// http요청 + url
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
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
    @DisplayName("게시글 작성 - 성공")
    void test5() throws Exception {
        //given
//        mockUserSetup();
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
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
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
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void test7() throws Exception {
        //given
        Long boardId = 1L;

        //when-then
        mockMvc.perform(delete("/api/boardmake/{boardId}", boardId))
                .andExpect(status().isOk()) //예상값
                .andDo(print());
    }
}