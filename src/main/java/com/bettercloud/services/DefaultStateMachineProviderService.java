package com.bettercloud.services;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import com.bettercloud.statemachine.actions.CompletionAction;
import com.bettercloud.statemachine.actions.EnrichmentAction;
import com.bettercloud.statemachine.actions.ExecutionAction;
import com.bettercloud.statemachine.actions.ValidationAction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Service
public class DefaultStateMachineProviderService implements StateMachineProviderService {

    private final StateMachineListener<String, String> stateMachineListener;
    private final BeanFactory beanFactory;

    private final ValidationAction validationAction;
    private final EnrichmentAction enrichmentAction;
    private final ExecutionAction executionAction;
    private final CompletionAction completionAction;

    public DefaultStateMachineProviderService(StateMachineListener<String, String> stateMachineListener, BeanFactory beanFactory,
                                              ValidationAction validationAction, EnrichmentAction enrichmentAction,
                                              ExecutionAction executionAction, CompletionAction completionAction) {
        this.stateMachineListener = stateMachineListener;
        this.beanFactory = beanFactory;
        this.validationAction = validationAction;
        this.enrichmentAction = enrichmentAction;
        this.executionAction = executionAction;
        this.completionAction = completionAction;
    }

    @Override
    public StateMachine<String, String> get() throws Exception {
        return get(UUID.randomUUID());
    }

    @Override
    public StateMachine<String, String> get(UUID id) throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .autoStartup(false)
                .listener(stateMachineListener)
                .machineId(Optional.ofNullable(id).orElse(UUID.randomUUID()).toString())
                .beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(States.VALIDATE)
                .end(States.COMPLETE)
                .stateDo(States.VALIDATE, validationAction)
                .stateDo(States.ENRICH, enrichmentAction)
                .stateDo(States.EXECUTE, executionAction)
                .stateDo(States.COMPLETE, completionAction);

        builder.configureTransitions()
                .withExternal()
                .source(States.VALIDATE).target(States.ENRICH).event(Events.FINISHED)
                .and()
                .withExternal()
                .source(States.VALIDATE).target(States.COMPLETE).event(Events.ERROR)
                .and()
                .withExternal()
                .source(States.ENRICH).target(States.EXECUTE).event(Events.FINISHED)
                .and()
                .withExternal()
                .source(States.ENRICH).target(States.COMPLETE).event(Events.ERROR)
                .and()
                .withExternal()
                .source(States.EXECUTE).target(States.COMPLETE).event(Events.FINISHED)
                .and()
                .withExternal()
                .source(States.EXECUTE).target(States.COMPLETE).event(Events.ERROR)
                .and()
                .withExit().source(States.COMPLETE);

        StateMachine<String, String> sm = builder.build();

        return sm;
    }
}
