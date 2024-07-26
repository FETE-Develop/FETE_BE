package fete.be.domain.payment.application.dto.response;

import lombok.Getter;

@Getter
public class TossPaymentResponse {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method;  // 결제 수단
    private int totalAmount;  // 총 결제 금액
    private int balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private boolean useEscrow;
    private String lastTransactionKey;  // nullable
    private int suppliedAmount;
    private int vat;
    private boolean cultureExpense;
    private int taxFreeAmount;
    private Integer taxExemptionAmount;
    private Cancel[] cancels;  // nullable
    private boolean isPartialCancelable;
    private Card card;  // nullable
    private VirtualAccount virtualAccount;  // nullable
    private String secret;  // nullable
    private MobilePhone mobilePhone;  // nullable
    private GiftCertificate giftCertificate;  // nullable
    private Transfer transfer;  // nullable
    private Receipt receipt;  // nullable
    private Checkout checkout;  // nullable
    private EasyPay easyPay;  // nullable
    private String country;
    private Failure failure;  // nullable
    private CashReceipt cashReceipt;  // nullable
    private CashReceipts[] cashReceipts;  // nullable
    private Discount discount;  // nullable


    @Getter
    public class Cancel {
        private int cancelAmount;
        private String cancelReason;
        private int taxFreeAmount;
        private Integer taxExemptionAmount;
        private int refundableAmount;
        private int easyPayDiscountAmount;
        private String canceledAt;  // ISO 8601 형식의 날짜 문자열
        private String transactionKey;
        private String receiptKey; // nullable
        private String cancelStatus;
        private String cancelRequestId; // nullable
    }

    @Getter
    public class Card {
        private int amount;
        private String issuerCode;
        private String acquirerCode;  // nullable
        private String number;
        private Integer installmentPlanMonths;
        private String approveNo;
        private boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private boolean isInterestFree;
        private String interestPayer;  // nullable
    }

    @Getter
    public class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private String dueDate;
        private String refundStatus;
        private boolean expired;
        private String settlementStatus;
        private RefundReceiveAccount refundReceiveAccount;
    }

    @Getter
    public class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;
    }

    @Getter
    public class MobilePhone {
        private String customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;
    }

    @Getter
    public class GiftCertificate {
        private String approveNo;
        private String settlementStatus;
    }

    @Getter
    public class Transfer {
        private String bankCode;
        private String settlementStatus;
    }

    @Getter
    public class Receipt {
        private String url;
    }

    @Getter
    public class Checkout {
        private String url;
    }

    @Getter
    public class EasyPay {
        private String provider;
        private int amount;
        private int discountAmount;
    }

    @Getter
    public class Failure {
        private String code;
        private String message;
    }

    @Getter
    public class CashReceipt {
        private String type;
        private String receiptKey;
        private String issueNumber;
        private String receiptUrl;
        private int amount;
        private int taxFreeAmount;
    }

    @Getter
    public class CashReceipts {
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String type;
        private String issueNumber;
        private String receiptUrl;
        private String businessNumber;
        private String transactionType;
        private Integer amount;
        private Integer taxFreeAmount;
        private String issueStatus;
        private Failure failure;
        private String customerIdentityNumber;
        private String requestedAt;
    }

    @Getter
    public class Discount {
        private Integer amount;
    }

}
