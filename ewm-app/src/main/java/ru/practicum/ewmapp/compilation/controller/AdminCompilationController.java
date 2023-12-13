package ru.practicum.ewmapp.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.compilation.dto.CompilationDto;
import ru.practicum.ewmapp.compilation.dto.NewCompilationDto;
import ru.practicum.ewmapp.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto dto){
        return compilationService.createCompilation(dto);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId,
                                            @RequestBody @Valid NewCompilationDto dto){
        return compilationService.updateCompilation(compilationId, dto);
    }

    @DeleteMapping("/{compilationId}")
            public void deleteCompilation(@PathVariable Long compilationId) {
        compilationService.deleteCompilation(compilationId);
    }
}
