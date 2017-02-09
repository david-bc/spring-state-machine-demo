package com.bettercloud.services;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.States;
import com.bettercloud.statemachine.actions.CompletionAction;
import com.bettercloud.statemachine.actions.EnrichmentAction;
import com.bettercloud.statemachine.actions.ExecutionAction;
import com.bettercloud.statemachine.actions.StateMachineAction;
import com.bettercloud.statemachine.actions.ValidationAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Service
public class DefaultStateMachineProviderService implements StateMachineProviderService {

    private final StateMachineListener<String, String> stateMachineListener;
    private final BeanFactory beanFactory;

    private final List<StateMachineAction> stateMachineActionBeans;

    public DefaultStateMachineProviderService(StateMachineListener<String, String> stateMachineListener, BeanFactory beanFactory,
                                              List<StateMachineAction> stateMachineActionBeans) {
        this.stateMachineListener = stateMachineListener;
        this.beanFactory = beanFactory;
        this.stateMachineActionBeans = stateMachineActionBeans;
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

        StateConfigurer<String, String> stateConfigurer = builder.configureStates()
                .withStates()
                .initial(States.VALIDATE)
                .end(States.COMPLETE);

        ExternalTransitionConfigurer<String, String> transitionConfigurer = builder.configureTransitions().withExternal();

        for (StateMachineAction action : stateMachineActionBeans) {
            log.info("Registering Action: {} => {}", action.getState(), action.getClass());
            stateConfigurer = stateConfigurer.stateDo(action.getState(), action);
            for (StateMachineTransition transition : action.getTransitions()) {
                log.info("Registering Transition: {} on {} => {}",
                        transition.getState(),
                        transition.getEvent(),
                        transition.getTarget());
                transitionConfigurer
                        .and()
                        .withExternal()
                        .source(transition.getState()).target(transition.getTarget()).event(transition.getEvent());
            }
        }

        StateMachine<String, String> sm = builder.build();

        sm.getTransitions().stream()
                .forEach(t -> log.info("Found Transition: {} on {} => {}", t.getSource().getId(), t.getTrigger().getEvent(), t.getTarget().getId()));

        return sm;
    }
}
