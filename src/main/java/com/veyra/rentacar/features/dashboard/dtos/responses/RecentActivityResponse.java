package com.veyra.rentacar.features.dashboard.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityResponse {
    private String id;
    private String type;
    private String title;
    private String subtitle;
    private LocalDateTime timestamp;
}
