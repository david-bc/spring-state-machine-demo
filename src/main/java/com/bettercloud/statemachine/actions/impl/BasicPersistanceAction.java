package com.bettercloud.statemachine.actions.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Service
public class BasicPersistanceAction implements Action<String, String> {

    private final StateMachinePersister<String, String, UUID> stateMachinePersister;

    public BasicPersistanceAction(StateMachinePersister<String, String, UUID> stateMachinePersister) {
        this.stateMachinePersister = stateMachinePersister;
    }

    @Override
    public void execute(StateContext<String, String> context) {
        StateMachine<String, String> stateMachine = context.getStateMachine();
        UUID id = UUID.fromString(stateMachine.getId());
        log.info("Persisting Transition: {}@{} => {}", id, stateMachine.getState().getId(), stateMachine.getExtendedState().getVariables());
        try {
            stateMachinePersister.persist(stateMachine, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
