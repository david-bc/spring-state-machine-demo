package com.bettercloud;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner stateMachineRunner(StateMachine<String, String> stateMachine) {
		return (args) -> {
			stateMachine.start();
			long delay = 0L;
			Thread.sleep(delay);
			stateMachine.sendEvent(Events.EVENT_1);
			Thread.sleep(delay);
			stateMachine.sendEvent(Events.EVENT_2);
			Thread.sleep(delay);
			stateMachine.sendEvent(Events.EVENT_1);
			Thread.sleep(delay);
			stateMachine.sendEvent(Events.EVENT_2);
			Thread.sleep(delay);
			stateMachine.stop();
			System.out.println(stateMachine.getExtendedState().getVariables());
			System.out.println(stateMachine.isComplete());
		};
	}
}
