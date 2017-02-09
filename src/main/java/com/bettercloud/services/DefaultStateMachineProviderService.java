package com.bettercloud.services;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
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

    private final BeanFactory beanFactory;

    private final Action<String, String> state1Action;
    private final Action<String, String> state2Action;

    public DefaultStateMachineProviderService(BeanFactory beanFactory,
                                              @Qualifier("state1Action") Action<String, String> state1Action,
                                              @Qualifier("state2Action")Action<String, String> state2Action) {
        this.beanFactory = beanFactory;
        this.state1Action = state1Action;
        this.state2Action = state2Action;
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
                .machineId(Optional.ofNullable(id).orElse(UUID.randomUUID()).toString())
                .beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(States.STATE_1)
                .stateDo(States.STATE_1, state1Action)
                .stateDo(States.STATE_2, state2Action);

        builder.configureTransitions()
                .withExternal()
                .source(States.STATE_1).target(States.STATE_2)
                    .event(Events.EVENT_1)
                .and()
                .withExternal()
                .source(States.STATE_2).target(States.STATE_1)
                    .event(Events.EVENT_2);

        StateMachine<String, String> sm = builder.build();

        return sm;
    }
}
