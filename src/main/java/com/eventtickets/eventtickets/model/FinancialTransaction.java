package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.eventtickets.eventtickets.user.User;

@Entity
@Getter
@Setter
@Table(name = "financial_transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Evento asociado a la transacción

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario que realizó el pago

    @Column(nullable = false)
    private double amount; // Monto total de la transacción

    @Column(nullable = false)
    private String transactionType; // Tipo de transacción (Ejemplo: "COMPRA", "REEMBOLSO")

    @Column(nullable = false)
    private double taxAmount; // Impuesto cobrado en la transacción

    @Column(nullable = false)
    private double netRevenue; // Ganancia neta después de impuestos

    @Column(nullable = false)
    private String paymentMethod; // Método de pago (Ejemplo: "Tarjeta", "Paypal")

    @Column(nullable = false)
    private String status; // Estado de la transacción (Ejemplo: "COMPLETADO", "FALLIDO")

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
