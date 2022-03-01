/*
 * (C) Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package com.hpe.onelytics.uncompress.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hpe.onelytics.uncompress.utilities.CommonUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class CommonUtilTest
{
    Config config;
    CommonUtil commonUtil;

    @BeforeEach
    public void setup()
    {
        config = ConfigFactory.defaultApplication();
        commonUtil = new CommonUtil();
    }

    @Test
    public void testCommonUtilCreated()
    {
        assertNotNull(commonUtil);
    }
}
