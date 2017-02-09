package com.bettercloud;

import com.bettercloud.services.StateMachineProviderService;
import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;

import java.util.Collection;

@Slf4j
@EnableStateMachine
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner stateMachineRunner(StateMachineProviderService stateMachineProviderService) {
		return (args) -> {
			StateMachine<String, String> stateMachine = stateMachineProviderService.get();

			stateMachine.start();

            Thread.sleep(500L);

			print(stateMachine);
            System.exit(0);
		};
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
