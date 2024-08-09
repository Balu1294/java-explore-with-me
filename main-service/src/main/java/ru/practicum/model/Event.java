package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "annotation", nullable = false, length = 2000)
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @Column(name = "create_date")
    LocalDateTime createdDate;
    @Column(name = "description", length = 7000)
    String description;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "initiator_id")
    User initiator;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "location_id")
    Location location;
    @Column(name = "paid")
    boolean paid;
    @Column(name = "participant_limit")
    int participantLimit;
    @Column(name = "published_date")
    LocalDateTime publisherDate;
    @Column(name = "request_moderation")
    boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status eventStatus;
    @Column(name = "title", nullable = false, length = 120)
    String title;
}
