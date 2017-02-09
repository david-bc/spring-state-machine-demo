package com.bettercloud;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.States;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

	public static final int LAPS_COUNT = 10000;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	/*
		@EnableStateMachine(contextEvents = true)

		StartTime: 1486612726896
		EndTime: 1486612729615
		Duration: 2719
		Laps: 10000
			max: 12
			min: 0
			avg: 0.2718
			steps/sec 3679.1758646063286
	 */

	/*
		@EnableStateMachine(contextEvents = false)

		StartTime: 1486612763932
		EndTime: 1486612765777
		Duration: 1845
		Laps: 10000
			max: 14
			min: 0
			avg: 0.1844
			steps/sec 5422.993492407809
	 */

	/*
	 * 	It is possible to achieve the
	 *
	 * 		0.2718 - 0.1844      0.0874
	 * 		---------------   =  ------  = 47.397% speedup
	 * 		     0.1844          0.1844
	 *
	 * 	by not using Spring's Context Events i.e. @EnableStateMachine(contextEvents = false)
	 * 	according to http://docs.spring.io/spring-statemachine/docs/1.2.1.RELEASE/reference/htmlsingle/#limitations-and-problems
	 */
	@Bean
	public CommandLineRunner stateMachineRunner(StateMachine<String, String> stateMachine, Timer timer) {
		return (args) -> {
			int iterations = LAPS_COUNT;
			timer.start();
			stateMachine.start();
			timer.stop();
			timer.clear();
			timer.print();

			timer.start();
			for (int i=0;i<iterations;i++) {
				stateMachine.sendEvent(Events.EVENT_1);
				stateMachine.sendEvent(Events.EVENT_2);
				timer.lap();
			}
			stateMachine.stop();
			timer.stop();

			timer.print();

			System.exit(0);
		};
	}

	@Bean
	public Timer timerBean() {
		return new Timer();
	}

	public static class Timer {
		private long startTime = System.currentTimeMillis();
		private long lapStartTime = System.currentTimeMillis();
		private List<Long> laps = new ArrayList<>(LAPS_COUNT);
		private long endTime = System.currentTimeMillis();

		public void start() {
			startTime = System.currentTimeMillis();
			lapStartTime = startTime;
		}

		public void lap() {
			long newLap = System.currentTimeMillis();
			laps.add(newLap - lapStartTime);
			lapStartTime = newLap;
		}

		public void stop() {
			endTime = System.currentTimeMillis();
		}

		public void clear() {
			laps.clear();
		}

		public void print() {
			System.out.println(this.toString());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("StartTime: ").append(this.startTime);
			sb.appendCodePoint('\n');
			sb.append("EndTime: ").append(this.endTime);
			sb.appendCodePoint('\n');
			sb.append("Duration: ").append(this.endTime - this.startTime);
			sb.appendCodePoint('\n');
			sb.append("Laps: ").append(this.laps.size());
			sb.append("\n\tmax: ").append(laps.stream().max((a,b) -> a.compareTo(b)).orElse(0L));
			sb.append("\n\tmin: ").append(laps.stream().min((a,b) -> a.compareTo(b)).orElse(0L));
			double avg = laps.stream().map(longNum -> (double)longNum).collect(Collectors.reducing((a, b) -> a + b)).orElse(1.0) / laps.size();
			sb.append("\n\tavg: ").append(avg);
			sb.append("\n\tsteps/sec ").append(1000.0/avg);
//			sb.append("\n\t").append(Joiner.on(", ").join(laps));
			return sb.toString();
		}
	}
}
