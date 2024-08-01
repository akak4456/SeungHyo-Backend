package com.adele.seunghyobackend.member.repository;

import com.adele.seunghyobackend.member.domain.MemberVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스 구성을 사용하도록 설정합니다. 이 설정이 없으면 Spring Boot는 자동으로 인메모리 데이터베이스를 사용할 수 있습니다.
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(true)
    @DisplayName("단순한 데이터를 한 번 저장하고 그 데이터를 한 번 다시 조회해본다.")
    public void simpleInitAndFind() {
        MemberVO member = new MemberVO("member1","password1","message1","Y","email1");
        memberRepository.save(member);

        MemberVO findMember = memberRepository.findById("member1").orElseThrow(() -> new RuntimeException("데이터는 반드시 존재해야 합니다."));

        assertThat(findMember).isEqualTo(member);

    }
}
