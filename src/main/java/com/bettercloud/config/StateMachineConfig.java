package com.bettercloud.config;

import com.bettercloud.repositories.StateMachineContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by davidesposito on 2/8/17.
 */
@Slf4j
@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachinePersister<String, String, UUID> stateMachinePersister(StateMachineContextRepository stateMachineContextRepository) {
        return new DefaultStateMachinePersister(stateMachineContextRepository);
    }

    @Bean
    @Primary
    public StateMachineListener<String, String> compositeListener(List<StateMachineListener<String, String>> listeners) {
        return new StateMachineListenerAdapter<String, String>() {
            @Override
            public void stateChanged(State<String, String> from, State<String, String> to) {
                listeners.forEach(listener -> listener.stateChanged(from, to));
            }
        };
    }

    @Bean
    public StateMachineListener<String, String> logListener() {
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
