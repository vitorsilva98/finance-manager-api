package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.finance.manager.api.enums.EntryTypeEnum;
import br.com.finance.manager.api.enums.PaymentMethodEnum;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AddEntryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amount;

    @PastOrPresent
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dateTime;

    @Size(min = 1, max = 255)
    private String description;

    @NotNull
    private UUID categoryId;

    @NotNull
    private PaymentMethodEnum paymentMethod;

    @NotNull
    private EntryTypeEnum type;
}
