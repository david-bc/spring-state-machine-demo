package com.bettercloud.config;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.actions.CompletionAction;
import com.bettercloud.statemachine.actions.EnrichmentAction;
import com.bettercloud.statemachine.actions.ExecutionAction;
import com.bettercloud.statemachine.actions.ValidationAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Optional;

/**
 * Created by davidesposito on 2/8/17.
 */
@Slf4j
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    @Bean
    public ValidationAction validationAction() {
        return (stateContext) -> {
            stateContext.getStateMachine().sendEvent(Events.FINISHED);
        };
    }

    @Bean
    public EnrichmentAction enrichmentAction() {
        return (stateContext) -> {
            stateContext.getStateMachine().sendEvent(Events.FINISHED);
        };
    }

    @Bean
    public ExecutionAction executionAction() {
        return (stateContext) -> {
            stateContext.getStateMachine().sendEvent(Events.FINISHED);
        };
    }

    @Bean
    public CompletionAction completionAction() {
        return (stateContext) -> {
            stateContext.getStateMachine().sendEvent(Events.FINISHED);
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
