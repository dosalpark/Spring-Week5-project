package com.sparta.team2newsfeed.repository;

import com.sparta.team2newsfeed.entity.Board;
import com.sparta.team2newsfeed.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup() throws InterruptedException {
        User user = User.builder().name("테스트").email("psh@naver.com").intro("자기소개").name("박승현").username("dosal").password("123456789").build();
        userRepository.save(user);
        Board board1 = Board.builder().title("김치담구기").body("재료준비").category("KOREAN").cookLevel(5).user(user).build();
        Board board2 = Board.builder().title("피클담구기").body("재료준비").category("WESTERN").cookLevel(3).user(user).build();
        Board board3 = Board.builder().title("락교담구기").body("재료준비").category("JAPANESE").cookLevel(1).user(user).build();
        Board board4 = Board.builder().title("단무지담구기").body("재료준비").category("CHINESE").cookLevel(1).user(user).build();
        Board board5 = Board.builder().title("물김치담구기").body("재료준비").category("KOREAN").cookLevel(5).user(user).build();
        boardRepository.save(board1);
        Thread.sleep(50);
        boardRepository.save(board2);
        Thread.sleep(50);
        boardRepository.save(board3);
        Thread.sleep(50);
        boardRepository.save(board4);
        Thread.sleep(50);
        boardRepository.save(board5);
    }

    @Test
    @DisplayName("모든 게시물 조회하기")
    void test1() {
        //given
        //when
        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
        //then
        //생성시간 역순정렬로 get(0)이 맨 마지막에 입력된 자료니까 get(0)의 시간이 더 느린지 확인
        LocalDateTime lastAddBoardDate = boards.get(0).getCreatedAt();
        LocalDateTime firstAddBoardDate = boards.get(4).getCreatedAt();
        assertTrue(lastAddBoardDate.isAfter(firstAddBoardDate));
        assertEquals(5, boards.size());
    }

    @Test
    @DisplayName("카테고리별 조회하기 - 성공")
    void test2() {
        //given
        String category = "KOREAN";
        //when
        List<Board> boards = boardRepository.findBoardsByCategoryEqualsOrderByCreatedAtDesc(category);
        //then
        LocalDateTime lastAddBoardDate = boards.get(0).getCreatedAt();
        LocalDateTime firstAddBoardDate = boards.get(1).getCreatedAt();
        assertTrue(lastAddBoardDate.isAfter(firstAddBoardDate));
        assertEquals(2, boards.size());
        assertEquals(category, boards.get(0).getCategory());
        assertEquals(category, boards.get(1).getCategory());
    }

    @Test
    @DisplayName("카테고리별 조회하기 - 카테고리 잘못입력")
    void test3() {
        //given
        String category = "KOREANA";
        //when
        List<Board> boards = boardRepository.findBoardsByCategoryEqualsOrderByCreatedAtDesc(category);
        //then
        assertTrue(boards.isEmpty());
    }

    @Test
    @DisplayName("난이도별 조회하기 - 성공")
    void test4() {
        //given
        int cookLevel = 5;
        //when
        List<Board> boards = boardRepository.findBoardsByCookLevelEqualsOrderByCreatedAtDesc(cookLevel);
        //then
        LocalDateTime lastAddBoardDate = boards.get(0).getCreatedAt();
        LocalDateTime firstAddBoardDate = boards.get(1).getCreatedAt();
        assertTrue(lastAddBoardDate.isAfter(firstAddBoardDate));
        assertEquals(2, boards.size());
    }

    @Test
    @DisplayName("난이도별 조회하기 - 난이도 잘못입력")
    void test5() {
        //given
        int cookLevel = 6;
        //when
        List<Board> boards = boardRepository.findBoardsByCookLevelEqualsOrderByCreatedAtDesc(cookLevel);
        //then
        assertTrue(boards.isEmpty());
    }
}