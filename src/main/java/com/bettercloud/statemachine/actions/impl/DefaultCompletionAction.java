package com.bettercloud.statemachine.actions.impl;

import com.bettercloud.statemachine.actions.SimpleStateMachineAction;
import com.bettercloud.statemachine.actions.CompletionAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.bettercloud.statemachine.States.COMPLETE;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Component
public class DefaultCompletionAction extends SimpleStateMachineAction implements CompletionAction {

    public DefaultCompletionAction() {
        super(COMPLETE, Collections.emptyList());
    }

    @Override
    protected void safeExecute(StateContext<String, String> context) {
        log.info("DONE!!! {}", context.getStateMachine().getId());
    }
}
