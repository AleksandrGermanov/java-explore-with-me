package ru.practicum.ewmapp.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class PaginationInfo {
    private Integer from;
    private Integer size;
    private Sort sort;

    public PaginationInfo(int from, int size){
        this.from = from;
        this.size = size;
    }

    public PageRequest asPageRequest(){
        return sort == null ? PageRequest.of(from/size, size)
                : PageRequest.of(from/size, size, sort);
    }
}
