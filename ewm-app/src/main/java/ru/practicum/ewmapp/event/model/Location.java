package ru.practicum.ewmapp.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @NotNull
    private Float lon;
    @NotNull
    private Float lat;
}