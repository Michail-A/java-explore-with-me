package ru.practicum.ewm.stat.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;
import ru.practicum.ewm.stat.server.service.ServerService;
import ru.practicum.ewm.stat.server.model.HitModel;

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
    public HitModel create(@RequestBody @Valid HitCreateDto hitCreateDto) {
        return serverService.create(hitCreateDto);
    }

    @GetMapping("/stats")
    public List<HitGetDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam (required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        return serverService.getStats(start, end, uris, unique);
    }
}
