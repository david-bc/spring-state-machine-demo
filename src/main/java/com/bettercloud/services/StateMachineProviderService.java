package com.bettercloud.services;

import org.springframework.statemachine.StateMachine;

import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
public interface StateMachineProviderService {

    StateMachine<String, String> get() throws Exception;

    StateMachine<String, String> get(UUID id) throws Exception;
}
