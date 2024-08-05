package com.adele.seunghyobackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.adele.seunghyobackend.TestConstant.INTEGRATED_TAG;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
// @ActiveProfiles("dev")
@Tag(INTEGRATED_TAG)
class SeungHyoBackendApplicationDevTests {

    @Test
    void contextLoads() {
    }

}
