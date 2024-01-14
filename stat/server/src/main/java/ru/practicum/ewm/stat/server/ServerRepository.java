package ru.practicum.ewm.stat.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stat.dto.HitDtoGet;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerRepository extends JpaRepository<HitModel, Integer> {
    @Query(value = "select new ru.practicum.ewm.stat.dto.HitDtoGet( h.app, h.uri, count(distinct(h.ip)))" +
            " from HitModel h where h.timestamp >= ?1 and h.timestamp <= ?2 " +
            "group by h.app, h.uri " +
            "order by 3 desc")
    List<HitDtoGet> getByNoUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.ewm.stat.dto.HitDtoGet(h.app, h.uri, count(distinct(h.ip)))" +
            " from HitModel h where h.timestamp >= ?1 and h.timestamp <= ?2 " +
            "group by h.app, h.uri " +
            "order by 3 desc")
    List<HitDtoGet> getByUnique(LocalDateTime start, LocalDateTime end);
}