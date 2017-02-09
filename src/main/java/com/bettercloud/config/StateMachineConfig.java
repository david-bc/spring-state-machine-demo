package com.bettercloud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Optional;

import static com.bettercloud.statemachine.States.STATE_1;
import static com.bettercloud.statemachine.States.STATE_2;

/**
 * Created by davidesposito on 2/8/17.
 */
@Slf4j
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    @Bean
    @Qualifier("state1Action")
    public Action<String, String> state1Action() {
        return stateAction(STATE_1);
    }

    @Bean
    @Qualifier("state2Action")
    public Action<String, String> state2Action() {
        return stateAction(STATE_2);
    }

    private Action<String, String> stateAction(String state) {
        return (stateContext) -> {
            System.out.println(state);
            ExtendedState extendedState = stateContext.getExtendedState();
            Integer count = Optional.ofNullable(extendedState.get(state, Integer.class)).orElse(0);
            extendedState.getVariables().put(state, count + 1);
        };
    }


    @Bean
    public StateMachineListener<String, String> listener() {
        return new StateMachineListenerAdapter<String, String>() {
            @Override
            public void stateChanged(State<String, String> from, State<String, String> to) {
                log.info(
                        "State change: {} => {}",
                        Optional.ofNullable(from).map(state -> state.getId()).orElse("*"),
                        Optional.ofNullable(to).map(state -> state.getId()).orElse("*")
                );
            }
        };
    }
}
