package com.bettercloud.statemachine.actions;

import com.bettercloud.statemachine.StateMachineTransition;
import org.springframework.statemachine.action.Action;

import java.util.List;

/**
 * Created by davidesposito on 2/9/17.
 */
public interface StateMachineAction extends Action<String, String> {

    boolean isPausable();

    String getState();

    List<StateMachineTransition> getTransitions();
}
