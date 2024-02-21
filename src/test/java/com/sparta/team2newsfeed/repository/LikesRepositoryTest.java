package com.sparta.team2newsfeed.repository;

import com.sparta.team2newsfeed.entity.Board;
import com.sparta.team2newsfeed.entity.Likes;
import com.sparta.team2newsfeed.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class LikesRepositoryTest {

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRepository boardRepository;

    User user;
    Board board;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .name("테스트")
                .email("psh@naver.com")
                .intro("자기소개")
                .name("박승현")
                .username("dosal")
                .password("123456789").build();
        userRepository.save(user);
        board = Board.builder()
                .title("김치담구기")
                .body("재료준비")
                .category("KOREAN")
                .cookLevel(5)
                .user(user).build();
        boardRepository.save(board);
    }

    @Test
    @DisplayName("findLikesByBoard_IdAndUser_IdEquals 쿼리메소드 검증 - 성공")
    void test1() {
        //given
        Likes addLikes = new Likes(user, board);
        likesRepository.save(addLikes);
        //when
        Likes likes = likesRepository.findLikesByBoard_IdAndUser_IdEquals(board.getId(), user.getId());
        //then
        assertEquals(likes.getUser().getId(), user.getId());
        assertEquals(likes.getBoard().getId(), board.getId());
    }

    @Test
    @DisplayName("findLikesByBoard_IdAndUser_IdEquals 쿼리메소드 검증 - board 제외하고 가져오나 확인")
    void test2() {
        //given
        Likes addLikes = new Likes(user, board);
        likesRepository.save(addLikes);
        //when
        Likes likes = likesRepository.findLikesByBoard_IdAndUser_IdEquals(null, user.getId());
        //then
        assertEquals(null, likes);
    }
}