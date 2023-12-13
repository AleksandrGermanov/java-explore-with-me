package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    @OneToMany(mappedBy = "compilation", fetch = FetchType.LAZY)
    private List<CompilationEventRelation> eventRelations;

    public Compilation(String title, Boolean pinned){
        this.title = title;
        this.pinned = pinned;
    }
}