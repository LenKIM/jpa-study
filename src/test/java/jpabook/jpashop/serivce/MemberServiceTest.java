package jpabook.jpashop.serivce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

  @Autowired
  private MemberService memberService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  void 회원가입() {

    Member member = new Member();
    member.setName("kim");

    Long saveId = memberService.join(member);

    assertThat(member).isEqualTo(memberRepository.findOne(saveId));
  }

  @Test
  void 중복_회원_예외() {

    Member member1 = new Member();
    member1.setName("kim");

    Member member2 = new Member();
    member2.setName("kim");

    assertThatThrownBy(() -> {
      memberService.join(member1);
      memberService.join(member2);
    }).isInstanceOf(IllegalStateException.class).hasMessageContaining("이미 존재하는 회원입니다.");
  }


}