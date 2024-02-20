package ru.practicum.ewm.event.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Status;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestParam extends RequestParam {
    private Collection<Integer> users;
    private Collection<Status> states;
}
