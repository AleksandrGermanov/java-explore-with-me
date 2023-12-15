package ru.practicum.ewmapp.compilation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Validated
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    @ToString.Exclude
    @OneToMany(mappedBy = "compilation")
    private List<CompilationEventRelation> eventRelations;

    public Compilation(String title, Boolean pinned) {
        this.title = title;
        this.pinned = pinned;
    }

    @ToString.Include
    public String relatedEventIds() {
        return eventRelations.stream()
                .map(r -> r.getEvent().getId())
                .collect(Collectors.toList())
                .toString();
    }
}