package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByCategory(Category category);

    List<Event> findAllByIdIn(List<Integer> ids);

    Optional<Event> findByInitiatorIdAndId(Integer userId, Integer eventId);

}
