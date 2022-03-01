//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertConditionNotNull implements AssertConditions
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
        log.info("assertCondition:" + assertCondition + ", receivedMessage:" + receivedMessage);
        if (receivedMessage.hasNonNull(assertCondition.get("notNull").asText()) == false)
        {
            log.error(
                    "AssertConditionNotNull failed. Expected: notNull. Actual:"
                            + receivedMessage.get(assertCondition.get("notNull").asText()));
            return false;
        }

        return true;
    }
}
