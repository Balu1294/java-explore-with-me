package ru.practicum.model;

import lombok.*;
import ru.practicum.enums.RequestStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
      Integer id;

    @Column(name = "create_date", nullable = false)
      LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
      Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
      User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RequestStatus status;
}
