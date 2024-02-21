package ru.practicum.ewm.event;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
