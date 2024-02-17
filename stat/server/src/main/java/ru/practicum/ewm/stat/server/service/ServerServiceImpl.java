package ru.practicum.ewm.stat.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;
import ru.practicum.ewm.stat.server.mapper.Mapper;
import ru.practicum.ewm.stat.server.model.HitModel;
import ru.practicum.ewm.stat.server.repository.ServerRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
    private final ServerRepository serverRepository;

    @Override
    public HitModel create(HitCreateDto hitCreateDto) {
        return serverRepository.save(Mapper.mapToNewHitModel(hitCreateDto));
    }

    @Override
    public List<HitGetDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Старт не может быть позже конца");
        }
        List<HitGetDto> stats = new ArrayList<>();
        if (unique) {
            stats = serverRepository.getByUnique(start, end);
        } else {
            stats = serverRepository.getByNoUnique(start, end);
        }
        List<HitGetDto> finalStat = new ArrayList<>();
        if (uri != null) {
            for (HitGetDto stat : stats) {
                if (uri.contains(stat.getUri())) {
                    finalStat.add(stat);
                }
            }
        } else {
            finalStat.addAll(stats);
        }
        return finalStat;
    }
}
