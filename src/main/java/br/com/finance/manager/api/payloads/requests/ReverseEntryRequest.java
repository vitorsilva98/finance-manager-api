package br.com.finance.manager.api.payloads.requests;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.PastOrPresent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReverseEntryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @PastOrPresent
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime reversalDateTime;
}
