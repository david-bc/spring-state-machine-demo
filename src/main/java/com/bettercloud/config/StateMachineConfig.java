package com.bettercloud.config;

import com.bettercloud.repositories.StateMachineContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

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
    public StateMachineMonitor<String, String> persistenceMonitor(StateMachinePersister<String, String, UUID> stateMachinePersister) {
        return new StateMachineMonitor<String, String>() {
            @Override
            public void transition(StateMachine<String, String> stateMachine, Transition<String, String> transition, long duration) {
                UUID id = UUID.fromString(stateMachine.getId());
                log.info("Persisting Transition: {}@{} => {}", id, stateMachine.getState().getId(), stateMachine.getExtendedState().getVariables());
                try {
                    stateMachinePersister.persist(stateMachine, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void action(StateMachine<String, String> stateMachine, Action<String, String> action, long duration) {
//                log.info("Persisting Action: {}@{} => {}", stateMachine.getId(), stateMachine.getState().getId(), stateMachine.getExtendedState().getVariables());
                // no-op
            }
        };
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
