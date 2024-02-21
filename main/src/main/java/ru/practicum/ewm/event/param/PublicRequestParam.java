package ru.practicum.ewm.event.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.EventSort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicRequestParam extends RequestParam {
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private EventSort sort;
}
