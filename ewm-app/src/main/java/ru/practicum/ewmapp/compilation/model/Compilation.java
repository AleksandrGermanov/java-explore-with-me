package ru.practicum.ewmapp.compilation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String title;
    private Boolean pinned;
    @OneToMany(mappedBy = "compilation", fetch = FetchType.LAZY)
    private List<CompilationEventRelation> eventRelations;
}