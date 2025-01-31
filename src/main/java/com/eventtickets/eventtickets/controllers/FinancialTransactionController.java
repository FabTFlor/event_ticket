package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.*;
import com.eventtickets.eventtickets.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/transactions")
public class FinancialTransactionController {

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // 📌 Obtener todas las transacciones de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTransactions(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<FinancialTransaction> transactions = financialTransactionRepository.findByUserId(userId);

        response.put("ncode", 1);
        response.put("transactions", transactions);
        return ResponseEntity.ok(response);
    }

    // 📌 Obtener todas las transacciones de un evento
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventTransactions(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        List<FinancialTransaction> transactions = financialTransactionRepository.findByEventId(eventId);

        response.put("ncode", 1);
        response.put("transactions", transactions);
        return ResponseEntity.ok(response);
    }

    // 📌 Registrar una transacción (Ejemplo: Pago de boleto)
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        double amount = ((Number) request.get("amount")).doubleValue();
        String transactionType = (String) request.get("transactionType");
        double taxAmount = ((Number) request.get("taxAmount")).doubleValue();
        String paymentMethod = (String) request.get("paymentMethod");

        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(eventId);

        if (user.isEmpty() || event.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Usuario o evento no encontrados.");
            return ResponseEntity.ok(response);
        }

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setUser(user.get());
        transaction.setEvent(event.get());
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setTaxAmount(taxAmount);
        transaction.setNetRevenue(amount - taxAmount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStatus("COMPLETADO");

        financialTransactionRepository.save(transaction);

        response.put("ncode", 1);
        response.put("message", "Transacción registrada con éxito.");
        response.put("transactionId", transaction.getId());
        return ResponseEntity.status(201).body(response);
    }
}
