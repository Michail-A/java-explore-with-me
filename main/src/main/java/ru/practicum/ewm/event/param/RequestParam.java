package ru.practicum.ewm.event.param;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
public class RequestParam {
    private LocalDateTime start;
    private LocalDateTime end;
    private Collection<Integer> categories;
    private Pageable page;
}
