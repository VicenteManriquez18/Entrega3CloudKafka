package com.proyecto.kafkaadmin.dto;

import jakarta.validation.constraints.Min;

import java.util.Map;

public class TopicConfigUpdateRequestDTO {

    private Map<String, String> configs;

    @Min(value = 1, message = "newPartitionCount debe ser mayor o igual a 1")
    private Integer newPartitionCount;

    public Map<String, String> getConfigs() { return configs; }
    public void setConfigs(Map<String, String> configs) { this.configs = configs; }

    public Integer getNewPartitionCount() { return newPartitionCount; }
    public void setNewPartitionCount(Integer newPartitionCount) { this.newPartitionCount = newPartitionCount; }
}

