package ru.practicum.ewmapp.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIdIn(List<Long> ids);
}
