package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.Status;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {


    Optional<Request> findByIdAndRequesterId(Integer id, Integer requesterId);

    List<Request> findAllByEventId(Integer eventId);


    List<Request> findAllByEventIdInAndStatus(List<Integer> eventIds, Status status);

    Boolean existsByEventIdAndRequesterId(Integer eventId, Integer userId);

    List<Request> findAllByRequesterId(Integer userId);

    int countByEventIdAndStatus(Integer eventId, Status status);


    Optional<List<Request>> findByEventIdAndIdIn(Integer eventId, List<Integer> id);
}
