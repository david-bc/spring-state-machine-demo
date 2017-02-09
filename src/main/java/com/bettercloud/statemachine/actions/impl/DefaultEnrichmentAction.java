package com.bettercloud.statemachine.actions.impl;

import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.actions.SimpleStateMachineAction;
import com.bettercloud.statemachine.actions.EnrichmentAction;
import com.google.common.collect.Lists;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bettercloud.statemachine.Events.ERROR;
import static com.bettercloud.statemachine.Events.FINISHED;
import static com.bettercloud.statemachine.States.COMPLETE;
import static com.bettercloud.statemachine.States.ENRICH;
import static com.bettercloud.statemachine.States.EXECUTE;

/**
 * Created by davidesposito on 2/9/17.
 */
@Component
public class DefaultEnrichmentAction extends SimpleStateMachineAction implements EnrichmentAction {

    public static final List<StateMachineTransition> TRANSITIONS = Collections.unmodifiableList(Lists.newArrayList(
            StateMachineTransition.builder().state(ENRICH).target(EXECUTE).event(FINISHED).build(),
            StateMachineTransition.builder().state(ENRICH).target(COMPLETE).event(ERROR).build()
    ));

    public DefaultEnrichmentAction() {
        super(ENRICH,  TRANSITIONS);
    }

    @Override
    protected void safeExecute(StateContext<String, String> context) {
        Boolean fail = Optional.ofNullable(context.getExtendedState().get("fail", Boolean.class)).orElse(false);
        String event = fail ? ERROR : FINISHED;
        context.getStateMachine().sendEvent(event);
    }
}
