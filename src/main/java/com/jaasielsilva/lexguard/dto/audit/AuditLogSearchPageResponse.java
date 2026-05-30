package com.jaasielsilva.lexguard.dto.audit;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogSearchPageResponse {

    private List<AuditLogResponse> items;
    private long totalElements;
    private int page;
    private int size;
    private boolean hasMore;
}
