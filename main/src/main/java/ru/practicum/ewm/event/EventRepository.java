package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<EventModel, Integer> {
    List<EventModel> findByInitiatorIdOrderByIdDesc(int userId, Pageable pageable);

    @Query("select e from EventModel e " +
            "where (coalesce(:userIds, null) is null or e.initiator.id in :userIds) " +
            "and (coalesce(:states, null) is null or e.state in :states) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) ")
    List<EventModel> findByAdmin(@Param("userIds") Collection<Integer> userIds,
                                 @Param("states") Collection<Status> states,
                                 @Param("categoryIds") Collection<Integer> categoryIds,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);

    Set<EventModel> findAllByIdIn(Set<Integer> eventsId);

    @Query("select e from EventModel e " +
            "where e.state = 'PUBLISHED' " +
            "and (coalesce(:text, null) is null or (lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%')))) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:paid, null) is null or e.paid = :paid) " +
            "and e.eventDate >= :rangeStart " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) " +
            "and (:onlyAvailable = false or e.id in " +
            "(select r.event.id " +
            "from RequestModel r " +
            "where r.status = 'CONFIRMED' " +
            "group by r.event.id " +
            "having e.participantLimit - count(id) > 0 " +
            "order by count(r.id))) ")
    List<EventModel> findAllPublic(@Param("text") String text, @Param("categoryIds") Collection<Integer> categoryIds,
                                   @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable,
                                   Pageable pageable);

    @Query("SELECT MIN(e.publishedOn) from EventModel e WHERE e.id in :eventsId")
    Optional<LocalDateTime> getCreatedOn(@Param("eventsId") Collection<Integer> eventIds);
}
