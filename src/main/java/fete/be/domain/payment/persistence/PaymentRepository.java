package fete.be.domain.payment.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaymentCode(String paymentCode);
    Optional<Payment> findByOrderId(String orderId);
}
