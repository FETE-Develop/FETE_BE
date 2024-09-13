package fete.be.domain.payment.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaymentCode(String paymentCode);
}
