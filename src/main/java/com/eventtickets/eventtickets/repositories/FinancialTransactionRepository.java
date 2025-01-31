package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    List<FinancialTransaction> findByUserId(Long userId); // Buscar transacciones por usuario
    List<FinancialTransaction> findByEventId(Long eventId); // Buscar transacciones por evento
}
