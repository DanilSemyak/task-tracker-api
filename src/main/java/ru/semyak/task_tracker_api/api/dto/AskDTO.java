package ru.semyak.task_tracker_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AskDTO {

    private Boolean answer;

    public static AskDTO makeDefault(Boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }
}
