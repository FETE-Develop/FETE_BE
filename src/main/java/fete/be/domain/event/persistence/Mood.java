package fete.be.domain.event.persistence;

import fete.be.domain.event.exception.InvalidMoodLengthException;
import fete.be.domain.event.exception.NotFoundMoodException;
import fete.be.global.util.ResponseMessage;

import java.util.Arrays;
import java.util.List;

public enum Mood {
    TRENDY("트렌디한"),
    HIP("힙한"),
    EXCITING("신나는"),
    EMOTIONAL("감성적인"),
    GROOVY("그루브한"),
    ACOUSTIC("어쿠스틱"),
    DREAMY("몽환적인"),
    INTENSE("강렬한"),
    CALM("잔잔한"),
    RETRO("레트로"),
    STYLISH("감각적인"),
    SOULFUL("소울풀한"),
    REFRESHING("청량한"),
    KPOP("케이팝"),
    BREEZY("산뜻한"),
    RELAXING("편안한"),
    GRAND("웅장한"),
    DARK("어두운");

    private String koreanValue;

    Mood(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public String getKoreanValue() {
        return this.koreanValue;
    }

    public static Mood convertMoodEnum(String koreanValue) {
        for (Mood mood : Mood.values()) {
            if (mood.getKoreanValue().equals(koreanValue)) {
                return mood;
            }
        }
        throw new NotFoundMoodException("일치하는 무드가 없습니다 : " + koreanValue);
    }

    // 무드의 최대 개수 및 올바른 값인지 검사
    public static String checkInvalidMoods(String moods) {
        List<String> compareMoods = Arrays.asList(moods.split(","));

        // 최대 선택은 3개까지만 가능
        if (compareMoods.size() > 3) {
            throw new InvalidMoodLengthException(ResponseMessage.EVENT_INVALID_MOOD_LENGTH.getMessage());
        }

        // 무드 리스트에 존재하는 값인지 검사
        for (String mood : compareMoods) {
            convertMoodEnum(mood);
        }

        return moods;
    }
}
