//(C) Copyright YYYY Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;

public class AssertConditionNotEquals implements AssertConditions
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
        final String equalElementCondition = assertCondition.get("notEqual").asText();
        final String[] condition = equalElementCondition.split(",");

        //Expected to be not equal, if not, then it is a failure
        if (receivedMessage.get(condition[0]).asText().equals(condition[1]))
        {
            log.error(
                    "AssertConditionNotEquals failed. Expected:"
                            + condition[1]
                            + ". Actual:"
                            + receivedMessage.get(condition[0]).asText());
            return false;
        }

        return true;
    }
}
