package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.finance.manager.api.enums.EntryTypeEnum;
import br.com.finance.manager.api.enums.PaymentMethodEnum;
import br.com.finance.manager.api.models.EntryModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private BigDecimal amount;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dateTime;
    private String description;
    private PaymentMethodEnum paymentMethod;
    private LocalDateTime reversalDateTime;
    private Boolean reversed;
    private EntryTypeEnum type;
    private String category;
    private String user;

    @SuppressWarnings("null")
    public EntryResponse(EntryModel entryModel) {
        BeanUtils.copyProperties(entryModel, this);
        this.amount = this.amount.setScale(2, RoundingMode.CEILING);
        this.category = entryModel.getCategory().getName();
        this.user = entryModel.getUser().getName();
    }
}
