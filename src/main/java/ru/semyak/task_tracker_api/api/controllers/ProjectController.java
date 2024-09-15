package ru.semyak.task_tracker_api.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.semyak.task_tracker_api.api.dto.AskDTO;
import ru.semyak.task_tracker_api.api.dto.ProjectDTO;
import ru.semyak.task_tracker_api.api.exceptions.BadRequestException;
import ru.semyak.task_tracker_api.api.exceptions.NotFoundException;
import ru.semyak.task_tracker_api.api.factories.ProjectDtoFactory;
import ru.semyak.task_tracker_api.store.entities.ProjectEntity;
import ru.semyak.task_tracker_api.store.repositories.ProjectRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@RestController
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectDtoFactory projectDtoFactory;

    public static final String FETCH_PROJECTS = "/api/projects";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";
    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDTO> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAll).stream();

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDTO createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
            // Another params...
    ) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();

        if (isCreate && !optionalProjectName.isPresent()) {
            throw new BadRequestException("Название проекта не может быть пустым.");
        }

        final ProjectEntity project = optionalProjectId
                .map(this::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestException(
                                        String.format("Проект \"%s\" уже существует.", projectName)
                                );
                            });

                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(savedProject);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDTO deleteProject(@PathVariable("project_id") Long projectId) {

        getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AskDTO.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {

        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Проекта с именем \"%s\" не существует.",
                                        projectId
                                )
                        )
                );
    }
}

