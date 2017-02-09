package com.bettercloud.statemachine.actions.impl;

import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.actions.AbstractStateMachineAction;
import com.bettercloud.statemachine.actions.EnrichmentAction;
import com.bettercloud.statemachine.actions.ValidationAction;
import com.google.common.collect.Lists;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.bettercloud.statemachine.Events.ERROR;
import static com.bettercloud.statemachine.Events.FINISHED;
import static com.bettercloud.statemachine.States.COMPLETE;
import static com.bettercloud.statemachine.States.ENRICH;
import static com.bettercloud.statemachine.States.EXECUTE;
import static com.bettercloud.statemachine.States.VALIDATE;

/**
 * Created by davidesposito on 2/9/17.
 */
@Component
public class DefaultValidationAction extends AbstractStateMachineAction implements ValidationAction {

    public static final List<StateMachineTransition> TRANSITIONS = Collections.unmodifiableList(Lists.newArrayList(
            StateMachineTransition.builder().state(VALIDATE).target(ENRICH).event(FINISHED).build(),
            StateMachineTransition.builder().state(VALIDATE).target(COMPLETE).event(ERROR).build()
    ));

    public DefaultValidationAction() {
        super(VALIDATE,  TRANSITIONS);
    }

    @Override
    protected void safeExecute(StateContext<String, String> context) {
        context.getStateMachine().sendEvent(FINISHED);
    }
}
