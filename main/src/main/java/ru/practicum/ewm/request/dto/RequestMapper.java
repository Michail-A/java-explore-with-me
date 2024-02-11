package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.RequestModel;
import ru.practicum.ewm.request.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto mapToRequestDto(RequestModel requestModel) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(requestModel.getId());
        requestDto.setRequester(requestModel.getRequester().getId());
        requestDto.setStatus(requestModel.getStatus());
        requestDto.setEvent(requestModel.getEvent().getId());
        requestDto.setCreated(requestModel.getCreateDate());
        return requestDto;
    }

    public static RequestStatusUpdateResult mapToRequestStatusUpdateResult(List<RequestModel> requests) {
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        RequestStatusUpdateResult requestStatusUpdateResult = new RequestStatusUpdateResult();

        for (RequestModel request : requests) {
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(RequestMapper.mapToRequestDto(request));
            }
            if (request.getStatus().equals(RequestStatus.REJECTED)) {
                rejectedRequests.add(RequestMapper.mapToRequestDto(request));
            }
        }
        requestStatusUpdateResult.setConfirmedRequests(confirmedRequests);
        requestStatusUpdateResult.setRejectedRequests(rejectedRequests);

        return requestStatusUpdateResult;
    }
}
