package com.icestudyroom_email.domain.rankingContract;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklyRankingDto {

    private int rank;
    private Long memberId; // Redis에 저장되는 사용자 식별자
    private String memberName; // 화면 표시용
    private int score;
}
