//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertPayloadElementsSize implements AssertConditions
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
        final int expectedPayloadElementsSize = assertCondition.get("payloadElementsSize").asInt();
        if (expectedPayloadElementsSize != receivedMessage.size())
        {
            log.error(
                    "AssertConditionNull failed. Expected:"
                            + expectedPayloadElementsSize
                            + ", Actual:"
                            + receivedMessage.size());
            return false;
        }

        return true;
    }
}
