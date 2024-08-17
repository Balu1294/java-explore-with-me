package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

@Data
@Entity(name = "location")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "lat")
    Double lat;
    @Column(name = "lon")
    Double lon;
}
