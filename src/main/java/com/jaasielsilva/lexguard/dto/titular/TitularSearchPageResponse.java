package com.jaasielsilva.lexguard.dto.titular;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TitularSearchPageResponse {

    private List<TitularResponse> items;
    private long totalElements;
    private int page;
    private int size;
    private boolean hasMore;
}
