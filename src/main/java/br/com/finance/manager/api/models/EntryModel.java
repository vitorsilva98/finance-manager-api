package br.com.finance.manager.api.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.finance.manager.api.enums.EntryTypeEnum;
import br.com.finance.manager.api.enums.PaymentMethodEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "entries")
public class EntryModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "payment_method", nullable = false)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private EntryTypeEnum type;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Boolean reversed;

    @Column(name = "reversal_date_time", nullable = true)
    private LocalDateTime reversalDateTime;

    @Column(nullable = true)
    private String description;
}
