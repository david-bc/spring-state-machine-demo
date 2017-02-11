package com.bettercloud.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.Maps;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Service
public class InMemoryStateMachineContextRepository implements StateMachineContextRepository {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Map<UUID, String> contextMap = Maps.newConcurrentMap();

    @Override
    public void write(StateMachineContext<String, String> context, UUID contextObj) throws Exception {
        String contextJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(context);
        contextMap.put(contextObj, contextJson);
    }

    @Override
    public StateMachineContext<String, String> read(UUID contextObj) throws Exception {
        return Optional.ofNullable(contextMap.get(contextObj))
                .map(jsonContext -> convert(jsonContext))
                .orElse(null);
    }

    private StateMachineContext<String, String> convert(String json) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            String id = getString(node, "id");
            String state = getString(node, "state");
            String event = getString(node, "event");
            Map<String, Object> eventHeaders = OBJECT_MAPPER.convertValue(node.path("eventHeaders"), Map.class);
            Map<String, String> historyStates = OBJECT_MAPPER.convertValue(node.path("historyStates"), Map.class);
            Map<Object, Object> stateVars = OBJECT_MAPPER.convertValue(node.path("extendedState").path("variables"), Map.class);
            ExtendedState extendedState = new DefaultExtendedState(stateVars);
            return new DefaultStateMachineContext<>(state, event, eventHeaders, extendedState, historyStates, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getString(JsonNode node, String key) {
        return Optional.ofNullable(node.get(key))
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText)
                .orElse(null);
    }

    @Override
    public StateMachineContext<String, String> remove(UUID id) {
        return null;
    }
}
