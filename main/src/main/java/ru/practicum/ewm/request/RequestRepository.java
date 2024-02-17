package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByEventIdAndEventInitiatorId(int eventId, int initiatorId);

    List<Request> findByEventId(int eventId);

    List<Request> findByRequesterId(int requesterId);

    List<Request> findByEventIdAndRequesterId(int eventId, int userId);
}
