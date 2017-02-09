package com.bettercloud.statemachine;

import lombok.Builder;
import lombok.Data;

/**
 * Created by davidesposito on 2/9/17.
 */
@Data
@Builder
public class StateMachineTransition {

    private final String state;
    private final String target;
    private final String event;
}
