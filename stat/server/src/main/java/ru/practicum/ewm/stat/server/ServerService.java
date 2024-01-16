package ru.practicum.ewm.stat.server;

import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerService {
    HitModel create(HitDtoCreate hitDtoCreate);

    List<HitDtoGet> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);
}
