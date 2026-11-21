package com.proyecto.kafkaadmin.controller;

import com.proyecto.kafkaadmin.dto.ApiResponseDTO;
import com.proyecto.kafkaadmin.dto.KafkaPublishRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicConfigUpdateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicCreateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicDetailDTO;
import com.proyecto.kafkaadmin.service.KafkaAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    public KafkaAdminController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @PostMapping("/topics")
    public ResponseEntity<ApiResponseDTO<Void>> createTopic(@Valid @RequestBody TopicCreateRequestDTO request) {
        kafkaAdminService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDTO<>(true, "Tópico '" + request.getName() + "' creado exitosamente.")
        );
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponseDTO<List<String>>> listTopics() {
        List<String> topics = kafkaAdminService.listTopics();
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Lista de tópicos obtenida exitosamente.", topics)
        );
    }

    @GetMapping("/topics/{name}")
    public ResponseEntity<ApiResponseDTO<TopicDetailDTO>> describeTopic(@PathVariable String name) {
        TopicDetailDTO detail = kafkaAdminService.describeTopic(name);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Detalle del tópico '" + name + "' obtenido exitosamente.", detail)
        );
    }

    @PutMapping("/topics/{name}/config")
    public ResponseEntity<ApiResponseDTO<Void>> updateTopicConfig(
            @PathVariable String name,
            @Valid @RequestBody TopicConfigUpdateRequestDTO request) {
        kafkaAdminService.updateTopicConfig(name, request);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Configuración del tópico '" + name + "' actualizada exitosamente.")
        );
    }

    @DeleteMapping("/topics/{name}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTopic(@PathVariable String name) {
        kafkaAdminService.deleteTopic(name);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Tópico '" + name + "' eliminado exitosamente.")
        );
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> publishMessage(
            @Valid @RequestBody KafkaPublishRequestDTO request) {
        Map<String, Object> metadata = kafkaAdminService.publishMessage(request);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Mensaje publicado exitosamente en '" + request.getTopic() + "'.", metadata)
        );
    }
}

