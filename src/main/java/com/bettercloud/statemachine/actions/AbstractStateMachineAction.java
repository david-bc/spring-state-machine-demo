package com.bettercloud.statemachine.actions;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.StateMachineTransition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Data
public abstract class AbstractStateMachineAction implements StateMachineAction {

    private final String state;
    private final List<StateMachineTransition> transitions;

    @Override
    public void execute(StateContext<String, String> context) {
        context.getExtendedState().getVariables().put(state, true);
        try {
            safeExecute(context);
        } catch (Exception e) {
            log.error("Failed execution", e);
            context.getStateMachine().sendEvent(Events.ERROR);
        }
    }

    protected abstract void safeExecute(StateContext<String, String> context) throws Exception;
}
