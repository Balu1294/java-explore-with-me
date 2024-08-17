package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseUpdatedStatusDto {
    private List<Integer> idsFromUpdateStatus;
    private List<Integer> processedIds;
}
