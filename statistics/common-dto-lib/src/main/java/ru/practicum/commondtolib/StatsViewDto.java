package ru.practicum.commondtolib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsViewDto {
    private String app;
    private String uri;
    private Long count;
}
