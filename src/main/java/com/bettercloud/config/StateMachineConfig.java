package com.bettercloud.config;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * Created by davidesposito on 2/8/17.
 */
@Slf4j
@Configuration
@EnableStateMachine(contextEvents = false)
public class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states)
            throws Exception {
        states
                .withStates()
                .initial(States.STATE_1)
                .states(States.ALL_STATES);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(States.STATE_1).target(States.STATE_2).event(Events.EVENT_1)
                .and()
                .withExternal()
                .source(States.STATE_2).target(States.STATE_1).event(Events.EVENT_2);
    }
}
