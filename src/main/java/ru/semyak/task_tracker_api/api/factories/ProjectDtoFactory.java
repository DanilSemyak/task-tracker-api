package ru.semyak.task_tracker_api.api.factories;


import org.springframework.stereotype.Component;
import ru.semyak.task_tracker_api.api.dto.ProjectDTO;
import ru.semyak.task_tracker_api.store.entities.ProjectEntity;

@Component
public class ProjectDtoFactory {

    public ProjectDTO makeProjectDto(ProjectEntity entity) {
        return ProjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
