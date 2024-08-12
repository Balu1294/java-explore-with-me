package ru.practicum.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseUpdatedStatusDto {
    private List<Integer> idsFromUpdateStatus;
    private List<Integer> processedIds;
}
