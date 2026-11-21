package com.proyecto.kafkaadmin.dto;

import jakarta.validation.constraints.NotBlank;

public class KafkaPublishRequestDTO {

    @NotBlank(message = "El tópico destino es obligatorio")
    private String topic;

    private String key;

    @NotBlank(message = "El payload no puede estar vacío")
    private String payload;

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}

