package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.CategoryModel;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.EventModel;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.users.UserModel;
import ru.practicum.ewm.users.dto.UserMapper;

import java.time.LocalDateTime;

public class EventMapper {

    public static EventModel mapToNewEventModel(EventDtoNew eventDtoNew, CategoryModel categoryModel, UserModel userModel) {
        EventModel eventModel = new EventModel();
        eventModel.setTitle(eventDtoNew.getTitle());
        eventModel.setAnnotation(eventDtoNew.getAnnotation());
        eventModel.setCategory(categoryModel);
        eventModel.setDescription(eventDtoNew.getDescription());
        eventModel.setCreatedOn(LocalDateTime.now());
        eventModel.setEventDate(eventDtoNew.getEventDate());
        eventModel.setInitiator(userModel);
        eventModel.setLocation(eventDtoNew.getLocation());
        eventModel.setPaid(eventDtoNew.getPaid());
        eventModel.setParticipantLimit(eventDtoNew.getParticipantLimit());
        eventModel.setState(Status.PENDING);
        eventModel.setViews(0);
        eventModel.setConfirmedRequests(0);
        eventModel.setRequestModeration(eventDtoNew.getRequestModeration());
        return eventModel;
    }

    public static EventFullDto mapToEventFullDto(EventModel eventModel) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(eventModel.getId());
        eventFullDto.setTitle(eventModel.getTitle());
        eventFullDto.setAnnotation(eventModel.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.mapToCategoryDtoGet(eventModel.getCategory()));
        eventFullDto.setDescription(eventModel.getDescription());
        eventFullDto.setInitiator(UserMapper.mapToUserShortDto(eventModel.getInitiator()));
        eventFullDto.setLocation(eventModel.getLocation());
        eventFullDto.setPaid(eventModel.getPaid());
        eventFullDto.setRequestModeration(eventModel.getRequestModeration());
        eventFullDto.setParticipantLimit(eventModel.getParticipantLimit());
        eventFullDto.setConfirmedRequests(eventModel.getConfirmedRequests());
        eventFullDto.setViews(eventModel.getViews());
        eventFullDto.setState(eventModel.getState().toString());
        eventFullDto.setEventDate(eventModel.getEventDate());
        eventFullDto.setCreatedOn(eventModel.getCreatedOn());
        if (eventModel.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(eventModel.getPublishedOn());
        }
        return eventFullDto;
    }

    public static EventShortDto mapToEventShortDto(EventModel eventModel) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(eventModel.getId());
        eventShortDto.setTitle(eventModel.getTitle());
        eventShortDto.setAnnotation(eventModel.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.mapToCategoryDtoGet(eventModel.getCategory()));
        eventShortDto.setInitiator(UserMapper.mapToUserShortDto(eventModel.getInitiator()));
        eventShortDto.setPaid(eventModel.getPaid());
        eventShortDto.setConfirmedRequests(eventShortDto.getConfirmedRequests());
        eventShortDto.setViews(eventModel.getViews());
        eventShortDto.setEventDate(eventModel.getEventDate());
        return eventShortDto;
    }
}
