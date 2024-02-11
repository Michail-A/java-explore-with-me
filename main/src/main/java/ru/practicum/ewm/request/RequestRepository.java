package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestModel, Integer> {
    List<RequestModel> findByEventIdAndRequesterId(int eventId, int requesterId);

    List<RequestModel> findByEventId(int eventId);

    List<RequestModel> findByRequesterId(int requesterId);
}
