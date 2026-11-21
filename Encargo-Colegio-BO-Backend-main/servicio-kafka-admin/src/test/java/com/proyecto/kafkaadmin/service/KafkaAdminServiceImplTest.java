package com.proyecto.kafkaadmin.service;

import com.proyecto.kafkaadmin.dto.KafkaPublishRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicConfigUpdateRequestDTO;
import com.proyecto.kafkaadmin.dto.TopicCreateRequestDTO;
import com.proyecto.kafkaadmin.exception.KafkaAdminException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.internals.KafkaFutureImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaAdminServiceImplTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaAdminServiceImpl service;

    @Test
    void createTopic_LlamaAdminClientCreateTopics() {
        CreateTopicsResult result = mock(CreateTopicsResult.class);
        KafkaFuture<Void> future = KafkaFutureImpl.completedFuture(null);
        when(result.all()).thenReturn(future);
        when(adminClient.createTopics(anyCollection())).thenReturn(result);

        TopicCreateRequestDTO dto = new TopicCreateRequestDTO();
        dto.setName("test.topic");
        dto.setPartitions(3);
        dto.setReplicas((short) 2);
        dto.setConfigs(Map.of("cleanup.policy", "delete"));

        service.createTopic(dto);
        verify(adminClient).createTopics(anyCollection());
    }

    @Test
    void listTopics_RetornaNombresOrdenados() {
        ListTopicsResult result = mock(ListTopicsResult.class);
        KafkaFuture<Set<String>> future = KafkaFutureImpl.completedFuture(Set.of("notas.eventos", "asistencia.eventos"));
        when(result.names()).thenReturn(future);
        when(adminClient.listTopics()).thenReturn(result);

        List<String> topics = service.listTopics();
        assertEquals(List.of("asistencia.eventos", "notas.eventos"), topics);
    }

    @Test
    void deleteTopic_LlamaAdminClientDeleteTopics() {
        DeleteTopicsResult result = mock(DeleteTopicsResult.class);
        KafkaFuture<Void> future = KafkaFutureImpl.completedFuture(null);
        when(result.all()).thenReturn(future);
        when(adminClient.deleteTopics(anyCollection())).thenReturn(result);

        service.deleteTopic("test.topic");
        verify(adminClient).deleteTopics(anyCollection());
    }

    @Test
    void updateTopicConfig_SinCambiosLanzaBadRequest() {
        TopicConfigUpdateRequestDTO empty = new TopicConfigUpdateRequestDTO();
        KafkaAdminException ex = assertThrows(KafkaAdminException.class,
                () -> service.updateTopicConfig("notas.eventos", empty));
        assertEquals(400, ex.getStatus());
    }
}

