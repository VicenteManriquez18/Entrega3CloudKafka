package com.proyecto.kafkaadmin.service;

import com.proyecto.kafkaadmin.dto.KafkaPublishRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicConfigUpdateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicCreateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicDetailDTO;

import java.util.List;
import java.util.Map;

public interface KafkaAdminService {

    void createTopic(TopicCreateRequestDTO request);

    List<String> listTopics();

    TopicDetailDTO describeTopic(String name);

    void updateTopicConfig(String name, TopicConfigUpdateRequestDTO request);

    void deleteTopic(String name);

    Map<String, Object> publishMessage(KafkaPublishRequestDTO request);
}

