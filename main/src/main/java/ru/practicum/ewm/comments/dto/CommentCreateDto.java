package ru.practicum.ewm.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {

    @NotBlank
    @Size(max = 7000)
    private String text;

    @NotNull
    private Integer eventId;
}