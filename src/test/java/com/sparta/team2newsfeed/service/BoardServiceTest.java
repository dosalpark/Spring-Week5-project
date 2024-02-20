package com.sparta.team2newsfeed.service;

import com.sparta.team2newsfeed.dto.AddBoardRequestDto;
import com.sparta.team2newsfeed.dto.AddBoardResponseDto;
import com.sparta.team2newsfeed.dto.BoardResponseDto;
import com.sparta.team2newsfeed.dto.StatusResponseDto;
import com.sparta.team2newsfeed.entity.Board;
import com.sparta.team2newsfeed.entity.Category;
import com.sparta.team2newsfeed.entity.User;
import com.sparta.team2newsfeed.imp.UserDetailsImpl;
import com.sparta.team2newsfeed.repository.BoardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) //테스트메소드 실행전 캐시랑 비우고 깨끗한 상태에서 테스트
@ActiveProfiles("test")
class BoardServiceTest {
    @Mock
    BoardRepository boardRepository;
    @Mock
    UserDetailsImpl userDetails;
    @InjectMocks
    BoardService boardService;

    List<BoardResponseDto> boardList;
    User user;
    User otherUser;
    Board board;

    @BeforeEach
    public void setup() {
        //사용하지 않는 메서드에도 추가되니 따로 data패키지 생성해서 분리하는게 좋음
        user = new User("dosal", "박승현", "123456789", "psh@g.g", "한마디");
        otherUser = new User("dosal1", "현승박", "987654321", "psh@gg.gg", "한마디");
        board = new Board("메인테스트", "내용", "KOREAN", 5, user);
        boardList = new ArrayList<>();
        BoardResponseDto board1 = new BoardResponseDto(new Board("테스트1", "내용1", "KOREAN", 5, user));
        BoardResponseDto board2 = new BoardResponseDto(new Board("테스트2", "내용2", "KOREAN", 3, user));
        BoardResponseDto board3 = new BoardResponseDto(new Board("테스트3", "내용3", "JAPANESE", 5, user));
        BoardResponseDto board4 = new BoardResponseDto(new Board("테스트4", "내용4", "JAPANESE", 1, user));
        BoardResponseDto board5 = new BoardResponseDto(new Board("테스트5", "내용5", "CHINESE", 2, user));
        BoardResponseDto board6 = new BoardResponseDto(new Board("테스트6", "내용6", "CHINESE", 3, user));
        BoardResponseDto board7 = new BoardResponseDto(new Board("테스트7", "내용7", "WESTERN", 4, user));
        BoardResponseDto board8 = new BoardResponseDto(new Board("테스트8", "내용8", "WESTERN", 5, user));
        boardList.add(board1);
        boardList.add(board2);
        boardList.add(board3);
        boardList.add(board4);
        boardList.add(board5);
        boardList.add(board6);
        boardList.add(board7);
        boardList.add(board8);
    }

    @AfterEach
    public void tearDown() {
        boardList.clear();
    }

    @Test
    @DisplayName("카테고리 검증 로직 - 실패")
    void test1() {
        //given
        String text = "Koreana";
        String upperCategoryName = text.toUpperCase();

        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
        {
            Category category1 = Category.valueOf(upperCategoryName);
        });

        //then
        assertEquals("No enum constant com.sparta.team2newsfeed.entity.Category." + upperCategoryName, exception.getMessage());
    }

    @Test
    @DisplayName("작성자 확인로직 - 성공/실패")
    void test2() {
        //given
        String falseUsername = "dododo";
        String falsePassword = "a123456789";
        String trueUsername = "dosal";
        String truePassword = "123456789";
        //when
        //then
        assertTrue(board.getUser().getUsername().equals(trueUsername));
        assertTrue(board.getUser().getPassword().equals(truePassword));
        assertFalse(board.getUser().getUsername().equals(falseUsername));
        assertFalse(board.getUser().getPassword().equals(falsePassword));
    }

    @Test
    @DisplayName("전체 게시물 조회 - 성공")
    void test3() {
        //given
        List<Board> boards = new ArrayList<>();
        boards.add(board);
        given(boardRepository.findAllByOrderByCreatedAtDesc()).willReturn(boards);
        //when
        ResponseEntity<?> response = boardService.getBoardAll();
        //then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("전체 게시물 조회 - 실패")
    void test4() {
        //given
        List<Board> boards = new ArrayList<>();
        given(boardRepository.findAllByOrderByCreatedAtDesc()).willReturn(boards);
        //when
        ResponseEntity<?> response = boardService.getBoardAll();
        //then
        assertEquals("현재 등록된 게시물이 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }


    @Test
    @DisplayName("단일 게시물 조회 - 성공")
    void test5() {
        //given
        Long boardId = 100L;
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        //when
        ResponseEntity<?> response = boardService.getBoardOne(boardId);
        //then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("단일 게시물 조회 - 실패")
    void test6() {
        //given
        Long boardId = 100L;
        given(boardRepository.findById(boardId)).willReturn(Optional.empty());
        //when
        ResponseEntity<?> response = boardService.getBoardOne(boardId);
        //then
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }

    @Test
    @DisplayName("난이도 별 조회 - 범위초과")
    void test7() {
        //given
        int cookLevel = 6;
        //when
        ResponseEntity<?> response = boardService.getBoardCookLevel(cookLevel);
        //then
        assertEquals("레시피의 난이도는 1~5까지 입니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }

    @Test
    @DisplayName("난이도 별 조회 - 게시글이 없을때")
    void test8() {
        //given
        int cookLevel = 3;
        //when
        ResponseEntity<?> response = boardService.getBoardCookLevel(cookLevel);
        //then
        assertEquals("난이도 " + cookLevel + " 에 해당하는 게시글이 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }

    @Test
    @DisplayName("카테고리 별 조회 - 카테고리가 맞지 않을때")
    void test9() {
        //given
        String category = "AAAAAA";
        //when
        ResponseEntity<?> response = boardService.getBoardCategory(category);
        //then
        assertEquals("현재 생성된 카테고리는 " + Arrays.toString(Category.values()) + "입니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }

    @Test
    @DisplayName("카테고리 별 조회 - 게시글이 없을때 + 카테고리명 대문자치환 확인")
    void test10() {
        //given
        String category = "korean";
        //when
        ResponseEntity<?> response = boardService.getBoardCategory(category);
        //then
        assertEquals(category + " 카테고리에 해당하는 레시피가 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
        assertEquals(400, ((StatusResponseDto) response.getBody()).getStatusCode());
    }

    @Test
    @DisplayName("게시글 작성 테스트 - 성공")
    void test11() {
        //given
        given(userDetails.getUser()).willReturn(user);
        AddBoardRequestDto requestDto = new AddBoardRequestDto("메인테스트", "내용", "KOREAN", 3, userDetails.getUser());
        given(boardRepository.save(Mockito.any(Board.class))).willReturn(board);
        //위처럼 사용하면 특정 Board를 save하는게아닌 아무 Board나 save했다고 생각하고 board를 반환해줌
        //given(boardRepository.save(new Board(requestDto, userDetails.getUser()))).willReturn(board);
        //when
        ResponseEntity<?> response = boardService.addBoard(userDetails, requestDto);
        //then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(((AddBoardResponseDto) response.getBody()).getTitle(), requestDto.getTitle());
        assertEquals(((AddBoardResponseDto) response.getBody()).getBody(), requestDto.getBody());
        assertEquals(((AddBoardResponseDto) response.getBody()).getCategory(), requestDto.getCategory());
        assertEquals(((AddBoardResponseDto) response.getBody()).getUsername(), requestDto.getUser().getUsername());
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 성공")
        //실패할수있는 요인인 작성자인지 검증과, 해당게시물은 위에서 이미 테스트 완료
    void test12() {
        //given
        Long boardId = board.getId();
        given(userDetails.getUser()).willReturn(user);
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(boardRepository.save(board)).willReturn(board);
        AddBoardRequestDto requestDto = new AddBoardRequestDto("변경title", "변경body,", "WESTREN", 3, userDetails.getUser());
        board.update(requestDto);
        //when
        ResponseEntity<?> response = boardService.updateBoard(boardId, userDetails, requestDto);
        //then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(((AddBoardResponseDto) response.getBody()).getTitle(), requestDto.getTitle());
        assertEquals(((AddBoardResponseDto) response.getBody()).getBody(), requestDto.getBody());
        assertEquals(((AddBoardResponseDto) response.getBody()).getCategory(), requestDto.getCategory());
        assertEquals(((AddBoardResponseDto) response.getBody()).getUsername(), requestDto.getUser().getUsername());
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 타인게시물")
    void test13() {
        //given
        Long boardId = board.getId();
        given(userDetails.getUser()).willReturn(otherUser);
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        AddBoardRequestDto requestDto = new AddBoardRequestDto("변경title", "변경body,", "WESTREN", 3, userDetails.getUser());
        //when
        ResponseEntity<?> response = boardService.updateBoard(boardId, userDetails, requestDto);
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("작성자만 수정이 가능합니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 게시물 존재하지않음")
    void test14() {
        //given
        given(userDetails.getUser()).willReturn(user);
        given(boardRepository.findById(Mockito.any(Long.class))).willReturn(Optional.empty());
        AddBoardRequestDto requestDto = new AddBoardRequestDto("변경title", "변경body,", "WESTREN", 3, userDetails.getUser());
        //when
        ResponseEntity<?> response = boardService.updateBoard(Mockito.any(Long.class), userDetails, requestDto);
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 성공")
    void test15() {
        //given
        Long boardId = board.getId();
        given(userDetails.getUser()).willReturn(user);
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        //when
        ResponseEntity<?> response = boardService.deleteBoard(boardId, userDetails);
        //then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("게시물이 삭제 되었습니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 타인게시물")
    void test16() {
        //given
        Long boardId = board.getId();
        given(userDetails.getUser()).willReturn(otherUser);
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        //when
        ResponseEntity<?> response = boardService.deleteBoard(boardId, userDetails);
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("작성자만 삭제가 가능합니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 게시물 존재하지않음")
    void test17() {
        //given
        given(boardRepository.findById(Mockito.any(Long.class))).willReturn(Optional.empty());

        //when
        ResponseEntity<?> response = boardService.deleteBoard(Mockito.any(Long.class), userDetails);
        //then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("해당하는 게시물이 없습니다.", ((StatusResponseDto) response.getBody()).getMsg());
    }
}