package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDto {
    @NotBlank
    @Size(max = 50)
    private String title;
    private Boolean pinned = false;
    private Set<Integer> events;
}
