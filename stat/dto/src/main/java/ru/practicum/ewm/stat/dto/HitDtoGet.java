package ru.practicum.ewm.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitDtoGet {
    private String app;
    private String uri;
    private long hits;
}
