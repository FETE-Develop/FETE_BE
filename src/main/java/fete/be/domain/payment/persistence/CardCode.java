package fete.be.domain.payment.persistence;

import fete.be.domain.payment.exception.NotFoundCardCodeException;

public enum CardCode {
    기업BC("3K"),
    광주은행("46"),
    롯데카드("71"),
    KDB산업은행("30"),
    BC카드("31"),
    삼성카드("51"),
    새마을금고("38"),
    신한카드("41"),
    신협("62"),
    씨티카드("36"),
    우리BC카드("33"),
    우리카드("W1"),
    우체국예금보험("37"),
    저축은행중앙회("39"),
    전북은행("35"),
    제주은행("42"),
    카카오뱅크("15"),
    케이뱅크("3A"),
    토스뱅크("24"),
    하나카드("21"),
    현대카드("61"),
    KB국민카드("11"),
    NH농협카드("91"),
    Sh수협은행("34");

    private String code;

    CardCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CardCode convertCardCode(String code) {
        for (CardCode cardCode : CardCode.values()) {
            if (cardCode.getCode().equals(code)) {
                return cardCode;
            }
        }
        throw new NotFoundCardCodeException("일치하는 카드사가 없습니다 : " + code);
    }
}
