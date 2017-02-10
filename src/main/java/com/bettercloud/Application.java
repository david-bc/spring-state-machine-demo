package com.bettercloud;

import com.bettercloud.services.ExecutionService;
import com.bettercloud.services.StateMachineProviderService;
import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bettercloud.statemachine.States.COMPLETE;
import static com.bettercloud.statemachine.States.ENRICH;
import static com.bettercloud.statemachine.States.EXECUTE;
import static com.bettercloud.statemachine.States.VALIDATE;

@Slf4j
@EnableStateMachine
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    private final AtomicBoolean successRunComplete = new AtomicBoolean(false);
    private final AtomicBoolean failRunComplete = new AtomicBoolean(false);
    private final AtomicBoolean loopRunComplete = new AtomicBoolean(false);
    private final AtomicBoolean pauseRunComplete = new AtomicBoolean(false);

    @Bean
    public CommandLineRunner success(ExecutionService executionService) {
        return (args) -> {
            HashMap<String, Object> params = Maps.newHashMap();
            StateMachine<String, String> stateMachine = executionService.accept(params);

            Thread.sleep(500L);

            print(stateMachine);

            assertEquals(true, stateMachine.isComplete(), "sm not complete");
            ExtendedState extendedState = stateMachine.getExtendedState();
            List<String> executed = Lists.newArrayList(VALIDATE, ENRICH, EXECUTE, COMPLETE);
            List<String> skipped = Lists.newArrayList();

            executed.forEach(state -> assertEquals(true, extendedState.get(state, Boolean.class), state + " didn't execute"));
            skipped.forEach(state -> assertEquals(null, extendedState.get(state, Boolean.class), state + " was execute"));

            successRunComplete.set(true);
        };
    }

    @Bean
    public CommandLineRunner fail(ExecutionService executionService) {
        return (args) -> {
            HashMap<String, Object> params = Maps.newHashMap();
            params.put("fail", true);
            StateMachine<String, String> stateMachine = executionService.accept(params);

            Thread.sleep(500L);

            print(stateMachine);

            assertEquals(true, stateMachine.isComplete(), "sm not complete");
            ExtendedState extendedState = stateMachine.getExtendedState();
            List<String> executed = Lists.newArrayList(VALIDATE, ENRICH, COMPLETE);
            List<String> skipped = Lists.newArrayList(EXECUTE);

            executed.forEach(state -> assertEquals(true, extendedState.get(state, Boolean.class), state + " didn't execute"));
            skipped.forEach(state -> assertEquals(null, extendedState.get(state, Boolean.class), state + " was execute"));

            failRunComplete.set(true);
        };
    }

    @Bean
    public CommandLineRunner loop(ExecutionService executionService) {
        return (args) -> {
            int loopCount = 5;
            HashMap<String, Object> params = Maps.newHashMap();
            params.put("loop", loopCount);
            StateMachine<String, String> stateMachine = executionService.accept(params);

            stateMachine.start();

            Thread.sleep(500L);

            print(stateMachine);

            assertEquals(true, stateMachine.isComplete(), "sm not complete");
            ExtendedState extendedState = stateMachine.getExtendedState();
            List<String> executed = Lists.newArrayList(VALIDATE, ENRICH, EXECUTE, COMPLETE);
            List<String> skipped = Lists.newArrayList();

            executed.forEach(state -> assertEquals(true, extendedState.get(state, Boolean.class), state + " didn't execute"));
            skipped.forEach(state -> assertEquals(null, extendedState.get(state, Boolean.class), state + " was execute"));
            assertEquals(loopCount, extendedState.get("loopCount", Integer.class), "Did not execute loop properly");

            loopRunComplete.set(true);
        };
    }

    @Bean
    public CommandLineRunner pause(ExecutionService executionService, StateMachineProviderService stateMachineProviderService) {
        return (args) -> {
            HashMap<String, Object> params = Maps.newHashMap();
            params.put("pause", true);
            StateMachine<String, String> stateMachine = executionService.accept(params);

            stateMachine.start();

            Thread.sleep(500L);

            stateMachine = stateMachineProviderService.get(UUID.fromString(stateMachine.getId()));
            stateMachine.getExtendedState().getVariables().put("pause", false);

            stateMachine.start();
            stateMachine.sendEvent(States.RESTART);

            Thread.sleep(500L);

            print(stateMachine);

            assertEquals(true, stateMachine.isComplete(), "sm not complete");
            ExtendedState extendedState = stateMachine.getExtendedState();
            List<String> executed = Lists.newArrayList(VALIDATE, ENRICH, EXECUTE, COMPLETE);
            List<String> skipped = Lists.newArrayList();

            executed.forEach(state -> assertEquals(true, extendedState.get(state, Boolean.class), state + " didn't execute"));
            skipped.forEach(state -> assertEquals(null, extendedState.get(state, Boolean.class), state + " was execute"));

            pauseRunComplete.set(true);
        };
    }

    @Bean
    public CommandLineRunner killer() {
        return (args) -> {
            new Thread(() -> {
                while (!successRunComplete.get() || !failRunComplete.get() || !loopRunComplete.get());
                System.exit(0);
            }).start();
        };
    }

    private void assertEquals(Object expected, Object actual, String error) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new RuntimeException(error + " (" + expected + " != " + actual + ")");
        }
    }

	private void print(StateMachine<String, String> sm) {
		log.info("id={} complete={} vars={} state={}",
				sm.getUuid().toString(),
				sm.isComplete(),
				sm.getExtendedState().getVariables().toString(),
                sm.getState().getId()
		);
	}
}
