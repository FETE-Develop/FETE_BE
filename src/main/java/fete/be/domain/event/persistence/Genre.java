package fete.be.domain.event.persistence;

import fete.be.domain.event.exception.NotFoundGenreException;

public enum Genre {
    EDM("EDM"),
    HOUSE("HOUSE"),
    TECHNO("TECHNO"),
    D_AND_B("D&B"),
    DISCO("DISCO"),
    JAZZ("JAZZ"),
    TRANCE("TRANCE"),
    AFROBEAT("AFROBEAT"),
    POP("POP");

    private String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    public String getKoreanValue() {
        return this.genre;
    }

    public static Genre convertGenreEnum(String koreanValue) {
        for (Genre genre : Genre.values()) {
            if (genre.getKoreanValue().equals(koreanValue)) {
                return genre;
            }
        }
        throw new NotFoundGenreException("일치하는 장르가 없습니다 : " + koreanValue);
    }
}
