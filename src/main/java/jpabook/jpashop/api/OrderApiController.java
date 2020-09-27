package jpabook.jpashop.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

  public OrderApiController(OrderRepository orderRepository,
      OrderQueryRepository orderQueryRepository) {
    this.orderRepository = orderRepository;
    this.orderQueryRepository = orderQueryRepository;
  }

  @GetMapping("/api/v1/orders")
  public List<Order> orderV1() {
    List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
    for (Order order : allByCriteria) {
      order.getMember().getName();
      order.getDelivery().getAddress();

      List<OrderItem> orderItems = order.getOrderItems();
      orderItems.forEach(o -> o.getItem().getName());
    }
    return allByCriteria;
  }

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {

    List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

    List<OrderDto> result = orders.stream()
        .map(OrderDto::new).collect(toList());
    return result;

  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithItem();

    List<OrderDto> result = orders.stream()
        .map(OrderDto::new).collect(toList());
    return result;

  }


  @GetMapping("/api/v3.1.1/orders")
  public List<OrderDto> ordersV3__1_page() {
    List<Order> orders = orderRepository.findAllWithMemberDelivery();
    List<OrderDto> result = orders.stream()
        .map(OrderDto::new).collect(toList());
    return result;
  }

  /**
   * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려 * - ToOne 관계만 우선 모두 페치 조인으로 최적화 * - 컬렉션 관계는
   * hibernate.default_batch_fetch_size, @BatchSize로 최적화
   */
  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> ordersV3_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
    List<OrderDto> result = orders.stream()
        .map(OrderDto::new).collect(toList());
    return result;
  }


  @Data
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {

      orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();
      orderItems = order.getOrderItems().stream()
          .map(OrderItemDto::new)
          .collect(toList());

    }

  }

  @Data
  static class OrderItemDto {

    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemDto(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }

  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
  }

  @GetMapping("/api/v5/orders")
  public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
  }

  @GetMapping("/api/v6/orders")
  public List<OrderQueryDto> ordersV6() {
    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
    // 중복이 발생하는 것을 직접 걸러내면 된다.
    return flats.stream()
        .collect(groupingBy(
            o -> new OrderQueryDto(
                o.getOrderId(),
                o.getName(),
                o.getOrderDate(),
                o.getOrderStatus(),
                o.getAddress()),
            mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(),
                    o.getCount()), toList()))).entrySet()
        .stream()
        .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
            e.getKey().getName(),
            e.getKey().getOrderDate(),
            e.getKey().getOrderStatus(),
            e.getKey().getAddress(),
            e.getValue())).collect(toList());
  }
}
