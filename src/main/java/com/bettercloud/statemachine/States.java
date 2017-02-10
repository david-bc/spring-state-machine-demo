package com.bettercloud.statemachine;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by davidesposito on 2/8/17.
 */
public class States {

    public static final String VALIDATE = "VALIDATE";
    public static final String ENRICH = "ENRICH";
    public static final String EXECUTE = "EXECUTE";
    public static final String COMPLETE = "COMPLETE";
    public static final String RESTART = "RESTART";

    public static final Set<String> ALL_STATES = Collections.unmodifiableSet(Sets.newHashSet(
            VALIDATE, ENRICH, EXECUTE, COMPLETE, RESTART
    ));
}
