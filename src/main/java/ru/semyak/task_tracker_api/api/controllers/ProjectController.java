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

    public static final String FETCH_PROJECTS = "api/projects";
    public static final String CREATE_PROJECT = "api/projects";
    public static final String EDIT_PROJECT = "api/projects/{project_id}";
    public static final String DELETE_PROJECT = "api/projects/{project_id}";
    public static final String CREATE_OR_UPDATE_PROJECTS = "api/projects";

    @PostMapping(FETCH_PROJECTS)
    public List<ProjectDTO> fetchProject(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAll).stream();

        if (optionalPrefixName.isPresent()) {
            projectStream = projectRepository.
                    streamAllByNameStartsWithIgnoreCase(optionalPrefixName.get()).stream();
        } else {
            projectStream = projectRepository.streamAll().stream();
        }

        return projectStream.map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDTO createProject(@RequestParam String name) {

        if (name.trim().isEmpty()) {
            throw new BadRequestException("Имя проекта не должно быть пустым");
        }

        projectRepository.findByName(name).ifPresent(project -> {
            throw new BadRequestException(String.format("Проект с именем \"%s\" уже существует", name));
        });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                        .build()
        );
        return projectDtoFactory.makeProjectDto(project);
    }


    @PatchMapping(EDIT_PROJECT)
    public ProjectDTO editProject(@PathVariable("project_id") Long projectId, @RequestParam String name) {

        if (name.trim().isEmpty()) {
            throw new BadRequestException("Имя проекта не должно быть пустым");
        }

        ProjectEntity project = getProjectOrThrowException(projectId);

        projectRepository.findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Проект с именем \"%s\" уже существует", name));
                });

        project.setName(name);

        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDTO deleteProject(@PathVariable("project_id") Long projectId) {

        ProjectEntity project = getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AskDTO.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Проект с id \"%s\" не существует", projectId)));
    }

}
