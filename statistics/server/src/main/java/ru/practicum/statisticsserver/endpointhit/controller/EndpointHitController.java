package ru.practicum.statisticsserver.endpointhit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
public class EndpointHitController {
    private final EndpointHitService endpointHitService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsViewDto> retrieveStatsViewList(@RequestParam LocalDateTime start,
                                                    @RequestParam LocalDateTime end,
                                                    @RequestParam(required = false) List<String> uris,
                                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получен запрос GET /stats; start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return endpointHitService.retrieveStatsViewList(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody EndpointHitDto dto) {
        log.info("Получен запрос POST /hit; dto = {}", dto);
        return endpointHitService.saveHit(dto);
    }
}
