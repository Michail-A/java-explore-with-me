package ru.practicum.ewm.stat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitDtoGet {
    private String app;
    private String uri;
    private Long hits;
}
