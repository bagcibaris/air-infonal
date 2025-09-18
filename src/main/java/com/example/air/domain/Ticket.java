package com.example.air.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "idx_ticket_number", columnList = "ticketNumber", unique = true)
})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Flight flight;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "ticket number is required")
    private String ticketNumber; // benzersiz bilet no

    @Column(nullable = false)
    @NotBlank(message = "passenger name is required")
    private String passengerName;

    @NotBlank(message = "passenger email is required")
    private String passengerEmail;

    @Column(nullable = false)
    private double pricePaid; // satın alım anındaki fiyat

    @Column(nullable = false)
    @NotBlank(message = "card number is required")
    private String maskedCardNumber; // 422116******0005 gibi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    private LocalDateTime purchasedAt;
}
