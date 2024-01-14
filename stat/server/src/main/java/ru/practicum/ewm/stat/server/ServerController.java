package ru.practicum.ewm.stat.server;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ServerController {
    private final ServerService serverService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitModel create(@RequestBody @Valid HitDtoCreate hitDtoCreate) {
        return serverService.create(hitDtoCreate);
    }

    @GetMapping
    public List<HitDtoGet> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam List<String> uri,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        return serverService.getStats(start, end, uri, unique);
    }
}
