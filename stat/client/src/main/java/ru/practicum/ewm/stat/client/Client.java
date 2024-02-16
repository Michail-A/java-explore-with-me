package ru.practicum.ewm.stat.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Client {
    @Value("${statistics.server.address}")
    private String serverUrl;

    private final RestTemplate restTemplate;


    public void create(HitDtoCreate hitDtoCreate) {
        restTemplate.postForLocation(serverUrl.concat("/hit"), hitDtoCreate);
    }

    public List<HitDtoGet> get(LocalDateTime start, LocalDateTime end,
                                         List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "unique", unique));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }

        HitDtoGet[] response = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                HitDtoGet[].class, parameters);

        return Objects.isNull(response)
                ? List.of()
                : List.of(response);
    }
}
