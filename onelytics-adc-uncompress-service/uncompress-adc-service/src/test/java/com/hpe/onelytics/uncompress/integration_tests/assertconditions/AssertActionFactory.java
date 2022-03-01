//(C) Copyright 2019 Hewlett Packard Enterprise Development LP
package com.hpe.onelytics.uncompress.integration_tests.assertconditions;

import com.fasterxml.jackson.databind.JsonNode;
import com.hpe.onelytics.uncompress.integration_tests.assertconditions.AssertConditions.AssertFunction;

public class AssertActionFactory
{
    public AssertFunction<JsonNode, JsonNode, Boolean> getAssertCondition(
            final JsonNode assertCondition,
            final JsonNode receivedMessge)
    {
        if (assertCondition.get("payloadElementsSize") != null)
        {
            return new AssertPayloadElementsSize().getAssertAction();
        }
        else if (assertCondition.get("Equal") != null)
        {
            return new AssertConditionEquals().getAssertAction();
        }
        else if (assertCondition.get("notEqual") != null)
        {
            return new AssertConditionNotEquals().getAssertAction();
        }
        else if (assertCondition.get("Null") != null)
        {
            return new AssertConditionNull().getAssertAction();
        }
        else if (assertCondition.get("notNull") != null)
        {
            return new AssertConditionNotNull().getAssertAction();
        }
        else if (assertCondition.get("noMessageProduced") != null)
        {
            return new AssertConditionNoMessage().getAssertAction();
        }
        return null;

    }
}
