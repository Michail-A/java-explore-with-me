package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam(required = false, name = "rangeStart")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false, name = "rangeEnd")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) Collection<Integer> users,
            @RequestParam(required = false) Collection<Integer> categories,
            @RequestParam(required = false) Collection<Status> states,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return eventService.getAllForAdmin(start, end, users, categories, states, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@RequestBody @Valid EventUpdateDto eventUpdateDto,
                                      @PathVariable int eventId) {
        return eventService.updateByAdmin(eventUpdateDto, eventId);
    }
}