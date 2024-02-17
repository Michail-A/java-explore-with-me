package ru.practicum.ewm.stat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitGetDto {
    private String app;
    private String uri;
    private Long hits;
}
