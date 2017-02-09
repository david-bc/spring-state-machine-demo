package com.bettercloud.services;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by davidesposito on 2/9/17.
 */
@Service
public class DefaultExecutionService implements ExecutionService {

    private final StateMachineProviderService stateMachineProviderService;

    public DefaultExecutionService(StateMachineProviderService stateMachineProviderService) {
        this.stateMachineProviderService = stateMachineProviderService;
    }

    @Override
    public StateMachine<String, String> accept(Map<String, Object> params) {
        StateMachine<String, String> stateMachine = stateMachineProviderService.get();

        params.entrySet().forEach(e -> stateMachine.getExtendedState().getVariables().put(e.getKey(), e.getValue()));

        stateMachine.start();

        return stateMachine;
    }
}
