package com.bettercloud.statemachine.actions.impl;

import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.actions.AbstractStateMachineAction;
import com.bettercloud.statemachine.actions.ExecutionAction;
import com.google.common.collect.Lists;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bettercloud.statemachine.Events.ERROR;
import static com.bettercloud.statemachine.Events.FINISHED;
import static com.bettercloud.statemachine.Events.REPEAT;
import static com.bettercloud.statemachine.States.COMPLETE;
import static com.bettercloud.statemachine.States.ENRICH;
import static com.bettercloud.statemachine.States.EXECUTE;

/**
 * Created by davidesposito on 2/9/17.
 */
@Component
public class DefaultExecutionAction extends AbstractStateMachineAction implements ExecutionAction {

    public static final List<StateMachineTransition> TRANSITIONS = Collections.unmodifiableList(Lists.newArrayList(
            StateMachineTransition.builder().state(EXECUTE).target(COMPLETE).event(FINISHED).build(),
            StateMachineTransition.builder().state(EXECUTE).target(COMPLETE).event(ERROR).build(),
            StateMachineTransition.builder().state(EXECUTE).target(ENRICH).event(REPEAT).build()
    ));

    public DefaultExecutionAction() {
        super(EXECUTE,  TRANSITIONS);
    }

    @Override
    protected void safeExecute(StateContext<String, String> context) {
        Integer loop = Optional.ofNullable(context.getExtendedState().get("loop", Integer.class)).orElse(0);
        String event = FINISHED;
        if (loop > 0) {
            context.getExtendedState().getVariables().put("loop", loop - 1);
            Integer currCount = Optional.ofNullable(context.getExtendedState().get("loopCount", Integer.class)).orElse(0);
            context.getExtendedState().getVariables().put("loopCount", currCount + 1);
            event = REPEAT;
        }
        context.getStateMachine().sendEvent(event);
    }
}
