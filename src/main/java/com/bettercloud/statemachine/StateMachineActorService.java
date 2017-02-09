package com.bettercloud.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Service;

import static com.bettercloud.statemachine.States.STATE_1;

/**
 * Created by davidesposito on 2/8/17.
 */
@Slf4j
@Service
@WithStateMachine
public class StateMachineActorService {

    @OnTransition(target = STATE_1)
    public void toState1(StateContext<String, String> stateContext) {
        // no-op
    }

    @OnTransition(target = States.STATE_2)
    public void toState2(StateContext<String, String> stateContext) {
        // no-op
    }
}
