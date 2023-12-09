package ru.practicum.ewmapp.util;
@FunctionalInterface
public interface Visitor<T> {
    void visit(T t);
}
