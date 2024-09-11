package ru.semyak.task_tracker_api.api.factories;

import org.springframework.stereotype.Component;
import ru.semyak.task_tracker_api.api.dto.TaskDTO;
import ru.semyak.task_tracker_api.store.entities.TaskEntity;

@Component
public class TaskDtoFactory {

    public TaskDTO makeTaskDto(TaskEntity entity) {

        return TaskDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .description(entity.getDescription())
                .build();
    }
}
