package ru.semyak.task_tracker_api.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semyak.task_tracker_api.store.entities.ProjectEntity;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByName(String name);

    Optional<ProjectEntity> streamAll();

    Optional<ProjectEntity> streamAllByNameStartsWithIgnoreCase(String prefixName);
}
