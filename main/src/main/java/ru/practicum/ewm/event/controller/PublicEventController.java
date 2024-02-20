package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventSort;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.param.PublicRequestParam;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.stat.client.StatsClient;
import ru.practicum.ewm.stat.dto.HitCreateDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Integer> categoriesIds,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @RequestParam(required = false) EventSort sort,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size,
                                      HttpServletRequest request) {

        Pageable page = PageRequest.of(from / size, size);
        PublicRequestParam param = new PublicRequestParam();
        param.setText(text);
        param.setCategories(categoriesIds);
        param.setPaid(paid);
        param.setStart(rangeStart);
        param.setEnd(rangeEnd);
        param.setOnlyAvailable(onlyAvailable);
        param.setSort(sort);
        param.setPage(page);

        sendHit(request);
        return eventService.getAll(param);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable int eventId, HttpServletRequest request) {
        sendHit(request);
        return eventService.getById(eventId);
    }

    private void sendHit(HttpServletRequest request) {
        HitCreateDto hitCreateDto = new HitCreateDto();
        hitCreateDto.setApp("ewm-main-service");
        hitCreateDto.setUri(request.getRequestURI());
        hitCreateDto.setIp(request.getRemoteAddr());
        hitCreateDto.setTimestamp(LocalDateTime.now());
        statsClient.create(hitCreateDto);
    }
}
