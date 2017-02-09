package com.bettercloud.statemachine.actions.impl;

import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.actions.AbstractStateMachineAction;
import com.bettercloud.statemachine.actions.ExecutionAction;
import com.google.common.collect.Lists;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.bettercloud.statemachine.Events.ERROR;
import static com.bettercloud.statemachine.Events.FINISHED;
import static com.bettercloud.statemachine.States.COMPLETE;
import static com.bettercloud.statemachine.States.EXECUTE;

/**
 * Created by davidesposito on 2/9/17.
 */
@Component
public class DefaultExecutionAction extends AbstractStateMachineAction implements ExecutionAction {

    public static final List<StateMachineTransition> TRANSITIONS = Collections.unmodifiableList(Lists.newArrayList(
            StateMachineTransition.builder().state(EXECUTE).target(COMPLETE).event(FINISHED).build(),
            StateMachineTransition.builder().state(EXECUTE).target(COMPLETE).event(ERROR).build()
    ));

    public DefaultExecutionAction() {
        super(EXECUTE,  TRANSITIONS);
    }

    @Override
    protected void safeExecute(StateContext<String, String> context) {
        context.getStateMachine().sendEvent(FINISHED);
    }
}
