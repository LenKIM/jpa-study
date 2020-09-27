package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  /**
   * 컬렉션은 별도로 조회 * Query: 루트 1번, 컬렉션 N 번 * 단건 조회에서 많이 사용하는 방식
   */
  public List<OrderQueryDto> findOrderQueryDtos() { //루트 조회(toOne 코드를 모두 한번에 조회)

    List<OrderQueryDto> result = findOrders(); //루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
      o.setOrderItems(orderItems);
    });
    return result;

  }

  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
            +
            " from  OrderItem oi" +
            " join  oi.item i" +
            " where  oi.order.id = : orderId", OrderItemQueryDto.class)
        .setParameter("orderId", orderId)
        .getResultList();
  }

  private List<OrderQueryDto> findOrders() {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
            +
            " from  Order o" +
            " join  o.member m" +
            " join  o.delivery d", OrderQueryDto.class).getResultList();

  }

  public List<OrderQueryDto> findAllByDto_optimization() {

    List<OrderQueryDto> result = findOrders();

    // ID 가 2개가 나올 것.
    List<OrderItemQueryDto> orderItems = findOrderItemMap(toOrderIds(result));

    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
        .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  private List<OrderItemQueryDto> findOrderItemMap(List<Long> orderIds) {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
            +
            " from  OrderItem oi" +
            " join  oi.item i" +
            " where  oi.order.id in :orderIds", OrderItemQueryDto.class)
        .setParameter("orderIds", orderIds)
        .getResultList();
  }

  private List<Long> toOrderIds(List<OrderQueryDto> result) {
    return result.stream().map(OrderQueryDto::getOrderId)
        .collect(Collectors.toList());
  }

  public List<OrderFlatDto> findAllByDto_flat() {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"
            +
            " from Order o" +
            " join o.member m" +
            " join o.delivery d" +
            " join o.orderItems oi" +
            " join oi.item i", OrderFlatDto.class).getResultList();

  }
}