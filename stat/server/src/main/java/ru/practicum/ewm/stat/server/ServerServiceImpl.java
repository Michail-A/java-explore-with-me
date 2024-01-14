package ru.practicum.ewm.stat.server;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
    private final ServerRepository serverRepository;

    @Override
    public HitModel create(HitDtoCreate hitDtoCreate) {
        return serverRepository.save(Mapper.mapToNewHitModel(hitDtoCreate));
    }

    @Override
    public List<HitDtoGet> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Старт не может быть позже конца");
        }
        List<HitDtoGet> stats = new ArrayList<>();
        if (unique) {
            stats = serverRepository.getByUnique(start, end);
        } else {
            stats = serverRepository.getByNoUnique(start, end);
        }
        List<HitDtoGet> finalStat = new ArrayList<>();
        for (HitDtoGet stat : stats) {
            if (uri.contains(stat.getUri())) {
                finalStat.add(stat);
            }
        }
        return finalStat;
    }
}
