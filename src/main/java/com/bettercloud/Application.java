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

	private StateMachine<String, String> sm1;
	private StateMachine<String, String> sm2;

	@Bean
	public CommandLineRunner stateMachineRunner(StateMachineProviderService stateMachineProviderService) {
		return (args) -> {
			StateMachine<String, String> stateMachine = stateMachineProviderService.get();
			sm1 = stateMachine;

			stateMachine.start();
			stateMachine.sendEvent(Events.EVENT_1);
			stateMachine.sendEvent(Events.EVENT_2);
			stateMachine.sendEvent(Events.EVENT_1);

			stateMachine.stop();

			print(stateMachine);

			Collection<String> stateNames = stateMachine.getState().getIds();
			if (stateNames.size() != 1 || !stateNames.contains(States.STATE_2)) {
				throw new RuntimeException();
			}
			Integer state1Count = stateMachine.getExtendedState().get(States.STATE_1, Integer.class);
			Integer state2Count = stateMachine.getExtendedState().get(States.STATE_2, Integer.class);
			if (state1Count != 2 || state2Count != 2) {
				throw new RuntimeException();
			}
		};
	}

	@Bean
	public CommandLineRunner stateMachineRunner2(StateMachineProviderService stateMachineProviderService) {
		return (args) -> {
			StateMachine<String, String> stateMachine = stateMachineProviderService.get();
			sm2 = stateMachine;

			Thread.sleep(1000L);

			stateMachine.start();
			stateMachine.stop();

			print(stateMachine);

			Collection<String> stateNames = stateMachine.getState().getIds();
			if (stateNames.size() != 1 || !stateNames.contains(States.STATE_1)) {
				throw new RuntimeException();
			}
			Integer state1Count = stateMachine.getExtendedState().get(States.STATE_1, Integer.class);
			Integer state2Count = stateMachine.getExtendedState().get(States.STATE_2, Integer.class);
			if (state1Count != 1 || state2Count != null) {
				throw new RuntimeException();
			}
		};
	}

	@Bean
	public CommandLineRunner killerRunner() {
		return (args) -> {
			new Thread(() -> {
				while (sm1 == null || sm2 == null);
				while (!sm1.isComplete() || !sm2.isComplete());
				System.exit(0);
			}).start();
		};
	}

	private void print(StateMachine<String, String> sm) {
		log.info("id={} complete={} vars={}",
				sm.getUuid().toString(),
				sm.isComplete(),
				sm.getExtendedState().getVariables().toString()
		);
	}
}
