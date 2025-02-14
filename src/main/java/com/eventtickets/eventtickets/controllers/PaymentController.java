package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.*;
import com.eventtickets.eventtickets.repositories.*;
import com.eventtickets.eventtickets.user.User;
import com.eventtickets.eventtickets.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    // 📌 Obtener pagos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPayments(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<Payment> payments = paymentRepository.findByUserId(userId);

        response.put("ncode", 1);
        response.put("payments", payments);
        return ResponseEntity.ok(response);
    }

    // 📌 Registrar un pago
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = ((Number) request.get("userId")).longValue();
        Long transactionId = ((Number) request.get("transactionId")).longValue();
        double amount = ((Number) request.get("amount")).doubleValue();
        String paymentMethod = (String) request.get("paymentMethod");

        Optional<User> user = userRepository.findById(userId);
        Optional<FinancialTransaction> transaction = financialTransactionRepository.findById(transactionId);

        if (user.isEmpty() || transaction.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Usuario o transacción no encontrados.");
            return ResponseEntity.ok(response);
        }

        Payment payment = new Payment();
        payment.setUser(user.get());
        payment.setTransaction(transaction.get());
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("COMPLETADO");

        paymentRepository.save(payment);

        response.put("ncode", 1);
        response.put("message", "Pago registrado con éxito.");
        response.put("paymentId", payment.getId());
        return ResponseEntity.status(201).body(response);
    }
}
