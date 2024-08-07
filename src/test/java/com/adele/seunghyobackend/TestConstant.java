package com.adele.seunghyobackend;

public class TestConstant {
    /**
     * 통합 테스트를 돌릴 때에는 항상 docker compose up
     * 을 실행해서 Mysql, Redis 등을 설정해야 한다.
     */
    public static final String INTEGRATED_TAG = "integrated";
    public static final String UNIT_TEST_TAG = "unit";
}
