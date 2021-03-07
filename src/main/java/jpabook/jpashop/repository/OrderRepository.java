package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  public List<Order> findAll2(OrderSearch orderSearch) {

    return em.createQuery("select o from Order o join o.member m" +
        " where o.status = :status" +
        " and m.name like :name", Order.class)
        .setParameter("status", orderSearch.getOrderStatus())
        .setParameter("name", orderSearch.getMemberName())
        .getResultList();
  }

  public List<Order> findAll(OrderSearch orderSearch) {

    //language=JPAQL
    String jpql = "select o From Order o join o.member m";
    boolean isFirstCondition = true;

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " o.status = :status";
    }

    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " m.name like :name";
    }

    TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건

    if (orderSearch.getOrderStatus() != null) {
      query = query.setParameter("status", orderSearch.getOrderStatus());
    }

    if (StringUtils.hasText(orderSearch.getMemberName())) {
      query = query.setParameter("name", orderSearch.getMemberName());
    }

    return query.getResultList();
  }


  public List<Order> findAllByCriteria(OrderSearch orderSearch) {
    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
    Root<Order> o = query.from(Order.class);
    Join<Object, Object> m = o.join("member", JoinType.INNER);

    //주문 상태 검색
    List<Predicate> criteria = new ArrayList<>();
    if (orderSearch.getOrderStatus() != null) {
      Predicate status = criteriaBuilder.equal(o.get("status"), orderSearch.getOrderStatus());
    }

    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      Predicate name = criteriaBuilder.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
      criteria.add(name);
    }

    query.where(criteriaBuilder.and(criteria.toArray(new Predicate[0])));
    TypedQuery<Order> query1 = em.createQuery(query).setMaxResults(1000);
    return query1.getResultList();
  }

  public List<Order> findAllWithMemberDelivery() {

    return em.createQuery(
        "SELECT o FROM  Order  o " +
            " JOIN FETCH o.member m" +
            " JOIN FETCH o.delivery d", Order.class).getResultList();

  }

  public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
        "SELECT o FROM  Order  o" +
            " JOIN FETCH o.member m" +
            " JOIN FETCH o.delivery d", Order.class
    ).setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }


  public List<Order> findAllWithItem() {
    return em.createQuery(
        "select distinct o from Order o" +
            " join fetch o.member m" +
            " join fetch o.delivery d" +
            " join fetch  o.orderItems oi" +
            " join fetch  oi.item i", Order.class
    ).setFirstResult(0)
        .setMaxResults(100)
        .getResultList();
  }


//  public List<Order> findAllString(OrderSearch orderSearch) {
//
//    String jpql = "select o from Order o join o.member m";
//    boolean isFirstCondition = true;
//
//    if (orderSearch.getOrderStatus() != ){
//
//    }
//  }
}
