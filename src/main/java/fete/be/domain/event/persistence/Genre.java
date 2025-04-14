package fete.be.domain.event.persistence;

import fete.be.domain.event.exception.InvalidGenreLengthException;
import fete.be.domain.event.exception.NotFoundGenreException;
import fete.be.global.util.ResponseMessage;

import java.util.Arrays;
import java.util.List;

public enum Genre {
    ACID("ACID"),
    AFROBEAT("AFROBEAT"),
    AMBIENT("AMBIENT"),
    BALEARIC("BALEARIC"),
    BASS("BASS"),
    DISCO("DISCO"),
    DOWNTEMPO("DOWNTEMPO"),
    DRILL("DRILL"),
    D_AND_B("D&B"),
    DUB("DUB"),
    EDM("EDM"),
    FUNK("FUNK"),
    GARAGE("GARAGE"),
    GRIME("GRIME"),
    HARDCORE("HARDCORE"),
    HIP_HOP("HIP-HOP"),
    HOUSE("HOUSE"),
    INDUSTRIAL("INDUSTRIAL"),
    ITALO_DISCO("ITALO-DISCO"),
    JAZZ("JAZZ"),
    JUNGLE("JUNGLE"),
    POP("POP"),
    R_AND_B("R&B"),
    TECHNO("TECHNO"),
    TRANCE("TRANCE"),
    DUBSTEP("DUBSTEP");

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

    // 장르의 최대 개수 및 올바른 값인지 검사
    public static String checkInvalidGenres(String genres) {
        List<String> compareGenres = Arrays.asList(genres.split(","));

        // 최대 선택은 3개까지만 가능
        if (compareGenres.size() > 3) {
            throw new InvalidGenreLengthException(ResponseMessage.EVENT_INVALID_GENRE_LENGTH.getMessage());
        }

        // 장르 리스트에 존재하는 값인지 검사
        for (String genre : compareGenres) {
            convertGenreEnum(genre);
        }

        return genres;
    }
}
