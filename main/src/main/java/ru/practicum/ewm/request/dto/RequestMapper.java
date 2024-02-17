package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setCreated(request.getCreateDate());
        return requestDto;
    }

    public static RequestStatusUpdateResult toRequestStatusUpdateResult(List<Request> requests) {
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        RequestStatusUpdateResult requestStatusUpdateResult = new RequestStatusUpdateResult();

        for (Request request : requests) {
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.toRequestDto(request));
            }
            if (request.getStatus().equals(RequestStatus.REJECTED)) {
                rejectedRequests.add(RequestMapper.toRequestDto(request));
            }
        }
        requestStatusUpdateResult.setConfirmedRequests(confirmedRequests);
        requestStatusUpdateResult.setRejectedRequests(rejectedRequests);

        return requestStatusUpdateResult;
    }
}
