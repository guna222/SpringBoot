//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertConditionEquals implements AssertConditions
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
        final String equalElementCondition = assertCondition.get("Equal").asText();
        final String[] condition = equalElementCondition.split(",");

        log.info("assertCondition:" + assertCondition);

        //Expected to be equal, if not, then it is a failure
        if (!receivedMessage.get(condition[0]).asText().equals(condition[1]))
        {
            log.error(
                    "AssertConditionEquals failed. Expected:"
                            + condition[1]
                            + ". Actual:"
                            + receivedMessage.get(condition[0]).asText());
            return false;
        }

        return true;
    }
}
