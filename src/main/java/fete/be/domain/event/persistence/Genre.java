package fete.be.domain.event.persistence;

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

    public String getGenre() {
        return this.genre;
    }
}
