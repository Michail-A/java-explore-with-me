package ru.practicum.ewm.stat.server.service;

import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;
import ru.practicum.ewm.stat.server.model.HitModel;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerService {
    HitModel create(HitCreateDto hitCreateDto);

    List<HitGetDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);
}
