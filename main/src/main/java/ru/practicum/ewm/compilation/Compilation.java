package ru.practicum.ewm.compilation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.Event;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "compilation", schema = "public")
@Getter
@Setter
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    Boolean pinned;

    String title;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "event_compilation",
            joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
}
