package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDtoNew;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.RequestStatusUpdateResult;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class EventControllerPrivate {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid EventDtoNew eventDtoNew, @PathVariable int userId) {
        return eventService.add(eventDtoNew, userId);
    }

    @GetMapping
    public List<EventShortDto> getAllByUser(@PathVariable int userId,
                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return eventService.getAllByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByIdByUser(@PathVariable int eventId, @PathVariable int userId) {
        return eventService.getByIdByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid EventUpdateDto eventUpdateDto,
                                    @PathVariable int eventId,
                                    @PathVariable int userId) {
        return eventService.updateEvent(eventUpdateDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable int userId, @PathVariable int eventId) {
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult updateRequestStatus(@RequestBody RequestStatusUpdateRequest requestStatusUpdateRequest,
                                                         @PathVariable int userId,
                                                         @PathVariable int eventId) {
        return eventService.updateRequestStatus(requestStatusUpdateRequest, eventId, userId);
    }
}
