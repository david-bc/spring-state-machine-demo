package com.bettercloud.statemachine;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by davidesposito on 2/8/17.
 */
public class States {

    public static final String STATE_1 = "STATE_1";
    public static final String STATE_2 = "STATE_2";

    public static final Set<String> ALL_STATES = Collections.unmodifiableSet(Sets.newHashSet(
            STATE_1,
            STATE_2
    ));
}
