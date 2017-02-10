package com.bettercloud.services;

import com.bettercloud.repositories.StateMachineContextRepository;
import com.bettercloud.statemachine.StateMachineTransition;
import com.bettercloud.statemachine.States;
import com.bettercloud.statemachine.actions.StateMachineAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.StateMachinePersister;
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
    private final BeanFactory beanFactory;
    private final StateMachineContextRepository stateMachineContextRepository;
    private final StateMachinePersister<String, String, UUID> stateMachinePersister;

    private final List<StateMachineAction> stateMachineActionBeans;

    public DefaultStateMachineProviderService(StateMachineListener<String, String> stateMachineListener,
                                              BeanFactory beanFactory,
                                              StateMachineContextRepository stateMachineContextRepository,
                                              StateMachinePersister<String, String, UUID> stateMachinePersister,
                                              List<StateMachineAction> stateMachineActionBeans) {
        this.stateMachineListener = stateMachineListener;
        this.beanFactory = beanFactory;
        this.stateMachineContextRepository = stateMachineContextRepository;
        this.stateMachinePersister = stateMachinePersister;
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
                log.info("Registering Action: {} => {}", action.getState(), action.getClass());
                for (StateMachineTransition transition : action.getTransitions()) {
                    transitionConfigurer
                            .and()
                            .withExternal()
                            .source(transition.getState()).target(transition.getTarget()).event(transition.getEvent());
                }
                if (action.isPausable()) {
                    transitionConfigurer
                            .and()
                            .withExternal()
                            .source(action.getState()).target(action.getState()).event(States.RESTART);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StateMachine<String, String> sm = builder.build();

        try {
            if (stateMachineContextRepository.read(id) != null) {
                stateMachinePersister.restore(sm, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sm;
    }
}
