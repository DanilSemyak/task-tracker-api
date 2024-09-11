package ru.semyak.task_tracker_api.api.factories;

import org.springframework.stereotype.Component;
import ru.semyak.task_tracker_api.api.dto.TaskStateDTO;
import ru.semyak.task_tracker_api.store.entities.TaskStateEntity;

@Component
public class TaskStateDtoFactory {

    public TaskStateDTO makeTaskStateDto(TaskStateEntity entity) {

        return TaskStateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordinal(entity.getOrdinal())
                .build();
    }
}
