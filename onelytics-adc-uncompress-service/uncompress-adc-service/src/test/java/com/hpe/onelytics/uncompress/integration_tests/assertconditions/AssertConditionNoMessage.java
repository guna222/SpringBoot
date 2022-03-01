//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertConditionNoMessage implements AssertConditions
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
        if (!receivedMessage.get("noMessageProduced").asText().equals("true"))
        {
            log.error(
                    "AssertConditionNoMessage failed. Expected: No message"
                            + ". Actual:"
                            + receivedMessage);
            return false;
        }

        return true;
    }
}
