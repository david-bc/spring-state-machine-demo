package com.bettercloud.repositories;

import com.google.common.collect.Maps;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Service
public class InMemoryStateMachineContextRepository implements StateMachineContextRepository {

    private final Map<UUID, StateMachineContext<String, String>> contextMap = Maps.newConcurrentMap();

    @Override
    public void write(StateMachineContext<String, String> context, UUID contextObj) throws Exception {
        contextMap.put(contextObj, context);
    }

    @Override
    public StateMachineContext<String, String> read(UUID contextObj) throws Exception {
        return contextMap.get(contextObj);
    }
}
