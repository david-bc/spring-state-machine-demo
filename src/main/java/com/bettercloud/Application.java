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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableStateMachine
@SpringBootApplication
public class Application {

    private static final int LAPS_COUNT = 40000;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    /*
        Builder Timer:
            StartTime: 1486621495480
            EndTime: 1486621499284
            Duration: 3804
            Laps: 120000
                max: 122
                min: 0
                avg: 0.0317
                laps/sec 31545.741324921135
     */
    @Bean
    public CommandLineRunner stateMachineRunner2(StateMachineProviderService stateMachineProviderService) {
        return (args) -> {
            int iters = LAPS_COUNT * 3;
            Timer timer = new Timer(iters);
            timer.start();
            for (int i=0;i<iters;i++) {
                stateMachineProviderService.get();
                timer.lap();
            }
            timer.stop();
            System.out.println("\n\nBuilder Timer:");
            timer.print();
        };
    }

    /*
        Build and Execution Timer:
            StartTime: 1486621499309
            EndTime: 1486621502107
            Duration: 2798
            Laps: 40000
                max: 107
                min: 0
                avg: 0.06995
                laps/sec 14295.925661186562
     */
    @Bean
    public CommandLineRunner stateMachineRunner(StateMachineProviderService stateMachineProviderService) {
        return (args) -> {
            Timer timer = new Timer(LAPS_COUNT);
            timer.start();
            for (int i=0;i<LAPS_COUNT;i++) {
                StateMachine<String, String> stateMachine = stateMachineProviderService.get();

                for (int j=0;j<10;j++) {
                    stateMachine.sendEvent(Events.EVENT_1);
                    stateMachine.sendEvent(Events.EVENT_2);
                }

                stateMachine.stop();

                timer.lap();
            }
            timer.stop();
            System.out.println("\n\nBuild and Execution Timer:");
            timer.print();
        };
    }

    /*
        Execution Timer:
            StartTime: 1486621599155
            EndTime: 1486621602840
            Duration: 3685
            Laps: 40000
                max: 54
                min: 0
                avg: 0.0921
                laps/sec 10857.763300760043
     */
    @Bean
    public CommandLineRunner stateMachineRunner3(StateMachineProviderService stateMachineProviderService) {
        return (args) -> {
            int iters = LAPS_COUNT;
            Timer timer = new Timer(iters);
            StateMachine<String, String> stateMachine = stateMachineProviderService.get();
            timer.start();
            stateMachine.start();
            for (int i=0;i<iters;i++) {
                stateMachine.sendEvent(Events.EVENT_1);
                stateMachine.sendEvent(Events.EVENT_2);

                timer.lap();
            }
            stateMachine.stop();
            timer.stop();
            System.out.println("\n\nExecution Timer:");
            timer.print();
        };
    }

    public static class Timer {
        private long startTime = System.currentTimeMillis();
        private long lapStartTime = System.currentTimeMillis();
        private List<Long> laps;
        private long endTime = System.currentTimeMillis();

        public Timer(int size) {
            laps = new ArrayList<>(size);
        }

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
            sb.append("\n\tlaps/sec ").append(1000.0/avg);
//			sb.append("\n\t").append(Joiner.on(", ").join(laps));
            return sb.toString();
        }
    }
}
