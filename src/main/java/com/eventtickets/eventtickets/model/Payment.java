package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario que realizó el pago

    @Column(nullable = false)
    private double amount; // Monto pagado

    @Column(nullable = false)
    private String paymentMethod; // Método de pago (Tarjeta, Paypal, etc.)

    @Column(nullable = false)
    private String status; // Estado del pago (COMPLETADO, PENDIENTE, FALLIDO)

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private FinancialTransaction transaction; // Transacción asociada al pago

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Fecha del pago
}
