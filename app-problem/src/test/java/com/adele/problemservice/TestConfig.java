package com.adele.problemservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class TestConfig {

}
