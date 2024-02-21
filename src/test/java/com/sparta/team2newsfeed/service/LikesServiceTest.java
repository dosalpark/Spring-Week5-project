package com.sparta.team2newsfeed.service;

import com.sparta.team2newsfeed.dto.CommentRequestDto;
import com.sparta.team2newsfeed.entity.Board;
import com.sparta.team2newsfeed.entity.Comment;
import com.sparta.team2newsfeed.entity.Likes;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.imp.UserDetailsImpl;
import com.sparta.team2newsfeed.repository.BoardRepository;
import com.sparta.team2newsfeed.repository.CommentRepository;
import com.sparta.team2newsfeed.repository.LikesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) //테스트메소드 실행전 캐시랑 비우고 깨끗한 상태에서 테스트
@ActiveProfiles("test")
class LikesServiceTest {

    @Mock
    BoardRepository boardRepository;
    @Mock
    LikesRepository likesRepository;
    @Mock
    UserDetailsImpl userDetails;
    @InjectMocks
    LikesService likesService;

    Likes likes;
    Board board;
    User user;
    User otherUser;

    @BeforeEach
    public void setup() {
        //사용하지 않는 메서드에도 추가되니 따로 data패키지 생성해서 분리하는게 좋음
        user = new User("dosal", "박승현", "123456789", "psh@g.g", "한마디");
        otherUser = new User("dosal1", "현승박", "987654321", "psh@gg.gg", "한마디");
        board = new Board("메인테스트", "내용", "KOREAN", 5, user);
        likes = new Likes();
    }

    @Test
    @DisplayName("좋아요")
    void test1(){
        //given
        Long boardId = 1L;
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(userDetails.getUser()).willReturn(user);
        given(likesRepository.findLikesByBoard_IdAndUser_IdEquals(boardId, userDetails.getUser().getId())).willReturn(null);
        //when
        likesService.voidLikes(userDetails, boardId);
        //then
        verify(likesRepository,times(1)).save(any(Likes.class));
        verify(likesRepository,never()).delete(any(Likes.class));
    }

    @Test
    @DisplayName("좋아요 취소")
    void test2(){
        //given
        Long boardId = 1L;
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(userDetails.getUser()).willReturn(user);
        given(likesRepository.findLikesByBoard_IdAndUser_IdEquals(boardId, userDetails.getUser().getId())).willReturn(likes);
        //when
        likesService.voidLikes(userDetails, boardId);
        //then
        verify(likesRepository,times(1)).delete(any(Likes.class));
        verify(likesRepository,never()).save(any(Likes.class));
    }

    @Test
    @DisplayName("좋아요 - 게시물 존재하지 않음")
    void test3(){
        //given
        Long boardId = 1L;
        given(boardRepository.findById(boardId)).willReturn(Optional.empty());
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                likesService.voidLikes(userDetails, boardId)
        );
        //then
        assertEquals("해당하는 게시물이 존재하지 않습니다.", exception.getMessage());
    }

}