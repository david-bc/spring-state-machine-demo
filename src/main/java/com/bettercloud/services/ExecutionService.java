package com.bettercloud.services;

import org.springframework.statemachine.StateMachine;

import java.util.Map;

/**
 * Created by davidesposito on 2/9/17.
 */
public interface ExecutionService {

    StateMachine<String, String> accept(Map<String, Object> params);
}
