//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertConditionNull implements AssertConditions
{
    @Override
    public AssertFunction<JsonNode, JsonNode, Boolean> getAssertAction()
    {
        return assertFunction;
    }

    private final AssertFunction<JsonNode, JsonNode, Boolean> assertFunction = (assertCondition, receivedMessage) -> {
        return execute(assertCondition, receivedMessage);
    };

    private Boolean execute(final JsonNode assertCondition, final JsonNode receivedMessage)
    {
        log.info("assertCondition:" + assertCondition);
        //Expected null in received message attribute
        if (receivedMessage.hasNonNull(assertCondition.get("Null").asText()) == true)
        {
            log.error(
                    "AssertConditionNull failed. Expected: null. Actual:"
                            + receivedMessage.get(assertCondition.get("Null").asText()));
            return false;
        }

        return true;
    }
}
