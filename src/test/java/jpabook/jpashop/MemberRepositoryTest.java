package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  public void di() {
    Assertions.assertThat(memberRepository).isNotNull();

    Member member = new Member();
    member.setName("Len");
    memberRepository.save(member);
    memberRepository.findAll();
  }
}
