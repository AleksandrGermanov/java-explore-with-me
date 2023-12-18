package ru.practicum.ewmapp.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    @Column(name = "name", nullable = false)
    private String name;
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    @Column(name = "email", nullable = false)
    private String email;
    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY)
    private List<ParticipationRequest> requestsFromUser;
    @OneToMany(mappedBy = "commentator", fetch = FetchType.LAZY)
    private List<Comment> userComments;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
