package com.sparta.team2newsfeed.dto;

import com.sparta.team2newsfeed.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddBoardRequestDto {

    private Long id;

    private String title;

    private String body;

    private String category;

    private int cookLevel;

    private User user;


    public AddBoardRequestDto(String title, String body, String category, int cookLevel, User user) {
        this.title = title;
        this.body = body;
        this.category = category;
        this.cookLevel = cookLevel;
        this.user = user;
    }
}