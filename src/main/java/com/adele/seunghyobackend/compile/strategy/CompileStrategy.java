package com.adele.seunghyobackend.compile.strategy;

import org.apache.commons.exec.ExecuteException;

import java.io.IOException;

/**
 * 주의!!! 상태를 가지고 있으므로
 * Bean 으로 관리하지 말고 new instant 생성자를 이용하도록 하자
 */
public interface CompileStrategy {
    void writeSourceCode(String sourceCode) throws IOException;

    void compile() throws IOException;

    String execute() throws IOException;

    void releaseResources();
}
