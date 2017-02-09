package com.bettercloud.repositories;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
public interface StateMachineContextRepository extends StateMachinePersist<String, String, UUID> {

    StateMachineContext<String, String> remove(UUID id);
}
