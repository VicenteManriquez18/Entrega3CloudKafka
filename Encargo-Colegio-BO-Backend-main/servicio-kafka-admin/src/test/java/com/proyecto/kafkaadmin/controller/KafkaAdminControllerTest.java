package com.proyecto.kafkaadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.kafkaadmin.config.SecurityConfig;
import com.proyecto.kafkaadmin.dto.KafkaPublishRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicConfigUpdateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicCreateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicDetailDTO;
import com.proyecto.kafkaadmin.service.KafkaAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaAdminController.class)
@Import(SecurityConfig.class)
class KafkaAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaAdminService kafkaAdminService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "Admin")
    void createTopic_ReturnsCreated() throws Exception {
        TopicCreateRequestDTO request = new TopicCreateRequestDTO();
        request.setName("demo.topic");
        request.setPartitions(3);
        request.setReplicas((short) 2);
        request.setConfigs(Map.of("retention.ms", "604800000", "cleanup.policy", "delete"));

        doNothing().when(kafkaAdminService).createTopic(any(TopicCreateRequestDTO.class));

        mockMvc.perform(post("/api/kafka/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void listTopics_ReturnsOk() throws Exception {
        when(kafkaAdminService.listTopics()).thenReturn(List.of("asistencia.eventos", "notas.eventos"));

        mockMvc.perform(get("/api/kafka/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("asistencia.eventos"));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void describeTopic_ReturnsOk() throws Exception {
        TopicDetailDTO detail = new TopicDetailDTO();
        detail.setName("notas.eventos");
        detail.setPartitions(3);
        detail.setReplicationFactor(2);
        detail.setConfigs(Map.of("cleanup.policy", "compact"));

        when(kafkaAdminService.describeTopic("notas.eventos")).thenReturn(detail);

        mockMvc.perform(get("/api/kafka/topics/notas.eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.partitions").value(3))
                .andExpect(jsonPath("$.data.configs['cleanup.policy']").value("compact"));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void updateTopicConfig_ReturnsOk() throws Exception {
        TopicConfigUpdateRequestDTO request = new TopicConfigUpdateRequestDTO();
        request.setConfigs(Map.of("retention.ms", "86400000"));
        request.setNewPartitionCount(4);

        doNothing().when(kafkaAdminService).updateTopicConfig(eq("demo.topic"), any(TopicConfigUpdateRequestDTO.class));

        mockMvc.perform(put("/api/kafka/topics/demo.topic/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void deleteTopic_ReturnsOk() throws Exception {
        doNothing().when(kafkaAdminService).deleteTopic("demo.topic");

        mockMvc.perform(delete("/api/kafka/topics/demo.topic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void publishMessage_ReturnsOk() throws Exception {
        KafkaPublishRequestDTO request = new KafkaPublishRequestDTO();
        request.setTopic("notas.eventos");
        request.setKey("1-1");
        request.setPayload("{\"notaId\":1,\"alumnoId\":1,\"calificacion\":6.5}");

        when(kafkaAdminService.publishMessage(any(KafkaPublishRequestDTO.class)))
                .thenReturn(Map.of("topic", "notas.eventos", "partition", 0, "offset", 10L));

        mockMvc.perform(post("/api/kafka/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topic").value("notas.eventos"));
    }

    @Test
    @WithMockUser(roles = "User")
    void nonAdminUser_IsForbidden() throws Exception {
        mockMvc.perform(get("/api/kafka/topics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticated_IsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/kafka/topics"))
                .andExpect(status().isUnauthorized());
    }
}

