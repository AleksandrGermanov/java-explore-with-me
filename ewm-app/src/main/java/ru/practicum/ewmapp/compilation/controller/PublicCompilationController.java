package ru.practicum.ewmapp.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

private final CompilationService compilationService;
    @GetMapping
    List<CompilationDto> findAllOrByPinnedParam(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero  Integer from,
                                                @RequestParam(defaultValue = "10") @Positive  Integer size){
        return compilationService.findAllOrByPinnedParam(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    CompilationDto retrieveCompilation(@PathVariable Long compilationId) {
        return compilationService.retrieveCompilation(compilationId);
    }
}
