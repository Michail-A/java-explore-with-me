package ru.practicum.ewm.stat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stat.dto.HitDtoCreate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Client extends BaseClient {

    public Client(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(HitDtoCreate hitDtoCreate) {
        return post("/hit", null, hitDtoCreate);
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uri", uri,
                "unique", unique
        );
        return get("/stats?start={start}&end={end]&uri={uri}&unique={unique}", parameters);
    }
}
