//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public interface AssertConditions
{
    final static Logger log = LoggerFactory.getLogger(AssertConditions.class.getSimpleName());

    @FunctionalInterface
    interface AssertFunction<A, B, R>
    {
        //R is like Return, but doesn't have to be last in the list nor named R.
        public R apply(A assertCondition, B receivedMessage);
    }

    public AssertFunction<JsonNode, JsonNode, Boolean> getAssertAction();
}
