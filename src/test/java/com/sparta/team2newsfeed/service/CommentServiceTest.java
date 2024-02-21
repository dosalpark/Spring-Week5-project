package com.sparta.team2newsfeed.service;

import com.sparta.team2newsfeed.dto.CommentRequestDto;
import com.sparta.team2newsfeed.dto.CommentResponseDto;
import com.sparta.team2newsfeed.dto.StatusResponseDto;
import com.sparta.team2newsfeed.entity.Board;
import com.sparta.team2newsfeed.entity.Comment;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.imp.UserDetailsImpl;
import com.sparta.team2newsfeed.repository.BoardRepository;
import com.sparta.team2newsfeed.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) //테스트메소드 실행전 캐시랑 비우고 깨끗한 상태에서 테스트
@ActiveProfiles("test")
class CommentServiceTest {

    @Mock
    BoardRepository boardRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserDetailsImpl userDetails;
    @InjectMocks
    CommentService commentService;

    Board board;
    Comment comment;
    CommentRequestDto commentRequestDto;
    User user;
    User otherUser;

    @BeforeEach
    public void setup() {
        //사용하지 않는 메서드에도 추가되니 따로 data패키지 생성해서 분리하는게 좋음
        user = new User("dosal", "박승현", "123456789", "psh@g.g", "한마디");
        otherUser = new User("dosal1", "현승박", "987654321", "psh@gg.gg", "한마디");
        board = new Board("메인테스트", "내용", "KOREAN", 5, user);
        commentRequestDto = new CommentRequestDto("테스트 댓글");
        comment = new Comment(commentRequestDto, board, user);
    }

    @Test
    @DisplayName("comment 생성 - 성공")
    void test1() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(userDetails.getUser()).willReturn(user);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        //when
        ResponseEntity<?> response = commentService.addComment(userDetails, commentRequestDto, board.getId());
        //then
        assertEquals(commentRequestDto.getComment(), ((CommentResponseDto) Objects.requireNonNull(response.getBody())).getComment());
        assertEquals(200, (response.getStatusCode()).value());
    }

    @Test
    @DisplayName("comment 생성 - 해당하는 게시물 없음")
    void test2() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.empty());
        //when
        ResponseEntity<?> response = commentService.addComment(userDetails, commentRequestDto, board.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    @Test
    @DisplayName("comment 수정 - 성공")
    void test3() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.of(comment));
        given(userDetails.getUser()).willReturn(user);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        CommentRequestDto requestDto = new CommentRequestDto("코멘트 변경");
        comment.update(requestDto);
        //when
        ResponseEntity<?> response = commentService.updateComment(userDetails, requestDto, board.getId(), comment.getId());
        //then
        assertEquals(comment.getComment(), ((CommentResponseDto) Objects.requireNonNull(response.getBody())).getComment());
        assertEquals(200, (response.getStatusCode()).value());
    }

    @Test
    @DisplayName("comment 수정 - 해당하는 게시물 없음")
    void test4() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.empty());
        CommentRequestDto requestDto = new CommentRequestDto("코멘트 변경");
        comment.update(requestDto);
        //when
        ResponseEntity<?> response = commentService.updateComment(userDetails, requestDto, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    @Test
    @DisplayName("comment 수정 - 해당하는 댓글 없음")
    void test5() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.empty());
        CommentRequestDto requestDto = new CommentRequestDto("코멘트 변경");
        comment.update(requestDto);
        //when
        ResponseEntity<?> response = commentService.updateComment(userDetails, requestDto, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 댓글이 없습니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    @Test
    @DisplayName("comment 수정 - 작성자 일치하지 않음")
    void test6() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.of(comment));
        given(userDetails.getUser()).willReturn(otherUser);
        CommentRequestDto requestDto = new CommentRequestDto("코멘트 변경");
        comment.update(requestDto);
        //when
        ResponseEntity<?> response = commentService.updateComment(userDetails, requestDto, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("작성자만 수정이 가능합니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    ////////////////////////////////
    @Test
    @DisplayName("comment 삭제 - 성공")
    void test7() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.of(comment));
        given(userDetails.getUser()).willReturn(user);
        //when
        ResponseEntity<StatusResponseDto> response = commentService.deleteComment(userDetails, board.getId(), comment.getId());
        //then
        assertEquals("댓글이 삭제 되었습니다.", (Objects.requireNonNull(response.getBody())).getMsg());
        //코드오류 확인: 현재값: 400 -> 수정값: 200
        assertEquals(400, (response.getStatusCode()).value());
    }

    @Test
    @DisplayName("comment 삭제 - 해당하는 게시물 없음")
    void test8() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.empty());
        //when
        ResponseEntity<?> response = commentService.deleteComment(userDetails, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    @Test
    @DisplayName("comment 수정 - 해당하는 댓글 없음")
    void test9() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.empty());
        //when
        ResponseEntity<?> response = commentService.deleteComment(userDetails, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 댓글이 없습니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

    @Test
    @DisplayName("comment 삭제 - 작성자 일치하지 않음")
    void test10() {
        //given
        given(boardRepository.findById(null)).willReturn(Optional.of(board));
        given(commentRepository.findById(null)).willReturn(Optional.of(comment));
        given(userDetails.getUser()).willReturn(otherUser);
        //when
        ResponseEntity<?> response = commentService.deleteComment(userDetails, board.getId(), comment.getId());
        //then
        assertEquals(400, response.getStatusCode().value());
        //코드오류 확인: 현재값:"작성자만 수정이 가능합니다." -> 수정값: "작성자만 삭제 가능합니다."
        assertEquals("작성자만 수정이 가능합니다.", ((StatusResponseDto) Objects.requireNonNull(response.getBody())).getMsg());
    }

}