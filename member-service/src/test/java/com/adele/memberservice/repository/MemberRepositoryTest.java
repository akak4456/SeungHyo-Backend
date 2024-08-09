package com.adele.memberservice.repository;

import com.adele.common.ValidFormUtil;
import com.adele.memberservice.DotenvTestExecutionListener;
import com.adele.memberservice.domain.Member;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Test
    @Rollback
    @DisplayName("find 가 되는지 간단히 확인해본다.")
    public void findTest() {
        Member member = new Member("user_test1", "pass1","status1", false, "email_test1", List.of("MEMBER"));
        memberRepository.save(member);
        Member findMember = memberRepository.findById("user_test1").get();
        assertThat(findMember.getMemberId()).isEqualTo("user_test1");
    }
}
