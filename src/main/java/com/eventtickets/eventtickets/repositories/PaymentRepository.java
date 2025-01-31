package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId); // Obtener pagos de un usuario
}
