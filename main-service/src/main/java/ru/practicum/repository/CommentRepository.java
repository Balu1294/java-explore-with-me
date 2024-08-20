package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.CountCommentsByEventDto;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByEvent_Id(Integer eventId, Pageable pageable);

    List<Comment> findByAuthor_Id(Integer userId);

    Optional<Comment> findByAuthor_IdAndId(Integer userId, Integer id);

    @Query("select c " +
            "from comments as c " +
            "where lower(c.text) like lower(concat('%', ?1, '%') )")
    List<Comment> search(String text, Pageable pageable);

//    @Query("select new ru.practicum.dto.CountCommentsByEventDto(c.event.id, COUNT(c)) " +
//            "from comments as c where c.event.id in ?1 " +
//            "GROUP BY c.event.id")
//    List<CountCommentsByEventDto> countCommentByEvent(List<Integer> eventIds);
}
