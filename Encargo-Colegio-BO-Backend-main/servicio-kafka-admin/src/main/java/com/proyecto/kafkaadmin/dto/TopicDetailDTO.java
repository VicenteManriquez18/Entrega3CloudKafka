package com.proyecto.kafkaadmin.dto;

import java.util.List;
import java.util.Map;

public class TopicDetailDTO {

    private String name;
    private int partitions;
    private int replicationFactor;
    private Map<String, String> configs;
    private List<Map<String, Object>> partitionDetails;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }

    public int getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(int replicationFactor) { this.replicationFactor = replicationFactor; }

    public Map<String, String> getConfigs() { return configs; }
    public void setConfigs(Map<String, String> configs) { this.configs = configs; }

    public List<Map<String, Object>> getPartitionDetails() { return partitionDetails; }
    public void setPartitionDetails(List<Map<String, Object>> partitionDetails) { this.partitionDetails = partitionDetails; }
}

