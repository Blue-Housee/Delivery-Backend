package com.spring.delivery.domain.config;

import com.spring.delivery.domain.fixture.UserFixtureGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test") // 테스트 전용 프로파일 적용
@Import({UserFixtureGenerator.class, TearDownExecutor.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTestBase {
    @Autowired
    protected UserFixtureGenerator userFixtureGenerator;

    @Autowired
    private TearDownExecutor tearDownExecutor;

    @BeforeEach
    void tearDown() {
        tearDownExecutor.execute();
    }
}
