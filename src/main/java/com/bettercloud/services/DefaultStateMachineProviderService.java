package com.bettercloud.services;

import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.States;
import com.bettercloud.statemachine.actions.StateMachineAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Service
public class DefaultStateMachineProviderService implements StateMachineProviderService {

    private final StateMachineListener<String, String> stateMachineListener;
    private final StateMachineMonitor<String, String> stateMachineMonitor;
    private final BeanFactory beanFactory;

    private final List<StateMachineAction> stateMachineActionBeans;

    public DefaultStateMachineProviderService(StateMachineListener<String, String> stateMachineListener,
                                              StateMachineMonitor<String, String> stateMachineMonitor,
                                              BeanFactory beanFactory,
                                              List<StateMachineAction> stateMachineActionBeans) {
        this.stateMachineListener = stateMachineListener;
        this.stateMachineMonitor = stateMachineMonitor;
        this.beanFactory = beanFactory;
        this.stateMachineActionBeans = stateMachineActionBeans;
    }

    @Override
    public StateMachine<String, String> get() {
        return get(UUID.randomUUID());
    }

    @Override
    public StateMachine<String, String> get(UUID id) {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        try {
            builder.configureConfiguration()
                    .withMonitoring()
                        .monitor(stateMachineMonitor)
                    .and()
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
                stateConfigurer = stateConfigurer.stateDo(action.getState(), action);
                for (StateMachineTransition transition : action.getTransitions()) {
                    transitionConfigurer
                            .and()
                            .withExternal()
                            .source(transition.getState()).target(transition.getTarget()).event(transition.getEvent());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StateMachine<String, String> sm = builder.build();

        return sm;
    }
}
