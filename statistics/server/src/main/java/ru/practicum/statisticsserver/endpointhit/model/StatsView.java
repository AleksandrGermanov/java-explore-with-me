package ru.practicum.statisticsserver.endpointhit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsView {
    private String app;
    private String uri;
    private Long hits;
}
