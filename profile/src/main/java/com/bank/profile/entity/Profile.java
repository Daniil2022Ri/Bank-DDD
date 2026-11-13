package com.bank.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "name_on_card")
    private String nameOnCard;

    @Column(name = "inn")
    private Long inn;

    @Column(name = "snils")
    private Long snils;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passport_id", nullable = false)
    private Passport passport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_registration_id")
    private ActualRegistration actualRegistration;
}
