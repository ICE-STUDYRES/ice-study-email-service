package com.icestudyroom_email.domain.email.infrastructure.kafka.dto.ranking;

import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import com.icestudyroom_email.domain.rankingContract.RankingEventType;
import org.springframework.stereotype.Component;

@Component
public class Top5EnterEmailTemplate implements RankingEmailTemplate {

    @Override
    public RankingEventType supports() {
        return RankingEventType.TOP5_ENTER;
    }

    @Override
    public EmailRequest create(RankingEmailEvent event) {
        return new EmailRequest(
                event.email(),
                "🏆 TOP5 진입! 열공하셨네요!! 🏆",
                """
                <div style="font-family: Arial, 'Apple SD Gothic Neo', sans-serif;
                            max-width: 520px;
                            margin: 0 auto;
                            padding: 24px;
                            background-color: #ffffff;
                            border-radius: 12px;
                            border: 1px solid #eaeaea;">
                    
                    <h2 style="margin-top: 0; color: #222;">
                        🎉 TOP5 진입을 축하드립니다! 🎉
                    </h2>
    
                    <p style="font-size: 15px; color: #444;">
                        <strong>%s</strong>님,
                    </p>
    
                    <p style="font-size: 15px; color: #444;">
                        꾸준한 노력 끝에 <strong style="color:#ff7a00;">TOP5</strong>에 진입하셨어요 👏  
                    </p>
    
                    <div style="background-color: #f9fafb;
                                padding: 16px;
                                border-radius: 10px;
                                margin: 20px 0;">
                        
                        <p style="margin: 4px 0; font-size: 14px;">
                            📌 <strong>이전 랭킹</strong> : %d위
                        </p>
                        <p style="margin: 4px 0; font-size: 14px;">
                            🚀 <strong>현재 랭킹</strong> : <span style="color:#2b6cff; font-weight: bold;">%d위</span>
                        </p>
                        <p style="margin: 4px 0; font-size: 14px;">
                            🔥 <strong>바로 위와의 점수 차</strong> : %d점
                        </p>
                    </div>
    
                    <p style="font-size: 14px; color: #555;">
                        이 페이스 그대로라면 <strong>1위</strong>도 머지않았어요 💪  
                    </p>
    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;" />
    
                    <p style="font-size: 12px; color: #999;">
                        ICE 스터디룸 랭킹 시스템에서 자동 발송된 메일입니다.
                    </p>
                </div>
                """.formatted(
                        event.name(),
                        event.previousRank(),
                        event.currentRank(),
                        event.gapWithUpper()
                )
        );
    }

}
