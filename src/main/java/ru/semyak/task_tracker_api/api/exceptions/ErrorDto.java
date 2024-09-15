package ru.semyak.task_tracker_api.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {

    private String error;

    @JsonProperty("error_description")
    String errorDescription;
}
