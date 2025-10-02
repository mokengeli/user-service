package com.bacos.mokengeli.biloko.infrastructure.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    private Long id;
    private String status;
    private String subject;
    private List<String> to;
    private String from;
    private OffsetDateTime createdAt;
}