package br.com.finance.manager.api.payloads.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import br.com.finance.manager.api.enums.EntryTypeEnum;
import br.com.finance.manager.api.enums.PaymentMethodEnum;
import br.com.finance.manager.api.models.EntryModel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EntryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String description;
    private PaymentMethodEnum paymentMethod;
    private LocalDateTime reversalDateTime;
    private Boolean reversed;
    private EntryTypeEnum type;
    private String category;
    private String user;

    public EntryResponse(EntryModel entryModel) {
        BeanUtils.copyProperties(entryModel, this);
        this.category = entryModel.getCategory().getName();
        this.user = entryModel.getUser().getName();
    }
}
