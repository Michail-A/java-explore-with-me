package ru.practicum.ewm.stat.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsClient {
    @Value("${statistics.server.address}")
    private String serverUrl;

    private final RestTemplate restTemplate;


    public void create(HitCreateDto hitCreateDto) throws HttpStatusCodeException {
        restTemplate.postForLocation(serverUrl.concat("/hit"), hitCreateDto);
    }

    public List<HitGetDto> get(LocalDateTime start, LocalDateTime end,
                               List<String> uris, boolean unique) throws HttpStatusCodeException {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "unique", unique));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }
        HitGetDto[] response = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                HitGetDto[].class, parameters);

        return Objects.isNull(response)
                ? List.of()
                : List.of(response);
    }
}
