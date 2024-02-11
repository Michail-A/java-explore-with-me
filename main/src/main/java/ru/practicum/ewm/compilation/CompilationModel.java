package ru.practicum.ewm.compilation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.EventModel;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "compilation", schema = "public")
@Getter
@Setter
@ToString
public class CompilationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    Boolean pinned;

    String title;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "event_compilation",
            joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<EventModel> events;
}
