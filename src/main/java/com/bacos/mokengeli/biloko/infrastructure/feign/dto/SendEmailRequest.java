package com.bacos.mokengeli.biloko.infrastructure.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {
    private String templateCode;
    private String tenantCode;
    private List<String> recipients;
    private Map<String, Object> variables;
    private String priority;  // HIGH, NORMAL, LOW, URGENT
}