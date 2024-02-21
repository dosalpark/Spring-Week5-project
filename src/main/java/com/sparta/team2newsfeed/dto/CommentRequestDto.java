package com.sparta.team2newsfeed.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long id;

    private String comment;

    public CommentRequestDto(String comment){
        this.comment = comment;
    }
}
