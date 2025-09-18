package com.example.air.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "airport name is required")
    private String name;

    @Column(nullable = false, unique = true, length = 3)
    @NotBlank(message = "code is required")
    private String code;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "country is required")
    private String country;
}
