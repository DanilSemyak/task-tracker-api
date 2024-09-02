package ru.semyak.task_tracker_api.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semyak.task_tracker_api.store.entities.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
