package fete.be.domain.event.persistence;

import fete.be.domain.event.exception.NotFoundMoodException;

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
}
