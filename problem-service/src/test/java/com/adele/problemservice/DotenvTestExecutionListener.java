package com.adele.problemservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.io.File;

public class DotenvTestExecutionListener implements TestExecutionListener {
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        File moduleRoot = new File(System.getProperty("user.dir"));
        Dotenv dotenv = Dotenv.configure()
                .directory(moduleRoot.getParentFile().getAbsolutePath())
                .load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}