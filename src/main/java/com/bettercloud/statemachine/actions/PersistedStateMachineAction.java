package com.bettercloud.statemachine.actions;

import com.bettercloud.statemachine.Events;
import com.bettercloud.statemachine.StateMachineTransition;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.List;
import java.util.UUID;

/**
 * Created by davidesposito on 2/9/17.
 */
@Slf4j
@Data
public abstract class PersistedStateMachineAction extends SimpleStateMachineAction {

    private final StateMachinePersister<String, String, UUID> stateMachinePersister;

    public PersistedStateMachineAction(String state, List<StateMachineTransition> transitions,
                                       StateMachinePersister<String, String, UUID> stateMachinePersister) {
        super(state, transitions);
        this.stateMachinePersister = stateMachinePersister;
    }

    @Override
    public void execute(StateContext<String, String> context) {
        super.execute(context);
        try {
            persist(context);
        } catch (Exception e) {
            log.error("Failed execution", e);
            context.getStateMachine().sendEvent(Events.ERROR);
        }
    }

    protected void persist(StateContext<String, String> context) throws Exception {
        StateMachine<String, String> stateMachine = context.getStateMachine();
        UUID id = UUID.fromString(stateMachine.getId());
        log.info("Persisting Transition: {}@{} => {}", id, stateMachine.getState().getId(), stateMachine.getExtendedState().getVariables());
        stateMachinePersister.persist(stateMachine, id);
    };
}
