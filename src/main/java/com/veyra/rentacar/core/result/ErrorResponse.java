package com.veyra.rentacar.core.result;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private List<String> errors;
    private int status;
    private LocalDateTime timestamp;
}
