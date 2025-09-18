package com.example.air.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Airline airline;

    @ManyToOne(optional = false)
    private Route route;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    @NotNull(message = "capacity is required")
    private int capacity;

    @Column(nullable = false)
    private int seatsSold;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull(message = "base price is required")
    private BigDecimal basePrice;

    @Version
    private Long version; // oversell'i önlemek için optimistic locking
}
