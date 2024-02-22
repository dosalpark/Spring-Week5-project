package com.sparta.team2newsfeed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private Long id;

    private String comment;

    public CommentRequestDto(String comment){
        this.comment = comment;
    }
}
