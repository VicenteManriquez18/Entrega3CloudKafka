package com.proyecto.kafkaadmin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class TopicCreateRequestDTO {

    @NotBlank(message = "El nombre del tópico es obligatorio")
    private String name;

    @Min(value = 1, message = "El número de particiones debe ser al menos 1")
    private int partitions = 3;

    @Min(value = 1, message = "El factor de replicación debe ser al menos 1")
    private short replicas = 2;

    private Map<String, String> configs;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }

    public short getReplicas() { return replicas; }
    public void setReplicas(short replicas) { this.replicas = replicas; }

    public Map<String, String> getConfigs() { return configs; }
    public void setConfigs(Map<String, String> configs) { this.configs = configs; }
}

