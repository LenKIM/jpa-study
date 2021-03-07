package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Qualifier(value = "MemberRepository")
@Repository
@RequiredArgsConstructor
public class MemberRepository {


    //    @PersistenceContext // JPA의 엔티티 매니저 주입
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
            .getResultList();

        //SQL은 테이블 중심으로, JPQL은 From의 대상이 - 객체 중심으로 간다.
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member  m where m.name = :name", Member.class)
            .setParameter("name",name)
            .getResultList();
    }
}

