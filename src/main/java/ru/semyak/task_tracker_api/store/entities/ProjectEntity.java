package ru.semyak.task_tracker_api.store.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String name;

    private Instant createdAt = Instant.now();

    @OneToMany
    private List<TaskStateEntity> taskStates;
}
