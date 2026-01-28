package com.icestudyroom_email.domain.email.infrastructure.template;

import com.icestudyroom_email.domain.email.infrastructure.gmail.dto.EmailRequest;
import com.icestudyroom_email.domain.rankingContract.RankingEmailEvent;
import com.icestudyroom_email.domain.rankingContract.RankingEventType;
import org.springframework.stereotype.Component;

@Component
public class Top5RankChangeEmailTemplate implements RankingEmailTemplate{

    @Override
    public RankingEventType supports() { return RankingEventType.TOP5_RANK_CHANGED; }

    @Override
    public EmailRequest create(RankingEmailEvent event) {

        boolean isUp = event.previousRank() != null
                && event.currentRank() < event.previousRank();

        String changeEmoji = isUp ? "📈" : "📉";
        String changeText = isUp ? "상승" : "변동";

        return new EmailRequest(
                event.email(),
                "🏆 TOP5 순위 변동 알림 🏆",
                """
                <div style="font-family: Arial, 'Apple SD Gothic Neo', sans-serif;
                            max-width: 520px;
                            margin: 0 auto;
                            padding: 24px;
                            background-color: #ffffff;
                            border-radius: 12px;
                            border: 1px solid #eaeaea;">
                    
                    <h2 style="margin-top: 0; color: #222;">
                        %s TOP5 순위 %s 알림
                    </h2>
    
                    <p style="font-size: 15px; color: #444;">
                        <strong>%s</strong>님,
                    </p>
    
                    <p style="font-size: 15px; color: #444;">
                        TOP5 순위권 내에서 <strong>순위 변동</strong>이 발생했어요!
                    </p>
    
                    <div style="background-color: #f9fafb;
                                padding: 16px;
                                border-radius: 10px;
                                margin: 20px 0;">
                        
                        <p style="margin: 4px 0; font-size: 14px;">
                            📌 <strong>이전 순위</strong> : %d위
                        </p>
                        <p style="margin: 4px 0; font-size: 14px;">
                            🚀 <strong>현재 순위</strong> :
                            <span style="color:#2b6cff; font-weight: bold;">%d위</span>
                        </p>
                        <p style="margin: 4px 0; font-size: 14px;">
                            🔥 <strong>바로 위와의 점수 차</strong> : %d점
                        </p>
                    </div>
    
                    <p style="font-size: 14px; color: #555;">
                        TOP5 경쟁이 치열해지고 있어요.<br/>
                        지금의 페이스를 유지해보세요 💪
                    </p>
    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;" />
    
                    <p style="font-size: 12px; color: #999;">
                        ICE 스터디룸 랭킹 시스템에서 자동 발송된 메일입니다.
                    </p>
                </div>
                """.formatted(
                        changeEmoji,
                        changeText,
                        event.name(),
                        event.previousRank(),
                        event.currentRank(),
                        event.gapWithUpper()
                )
        );
    }
}
