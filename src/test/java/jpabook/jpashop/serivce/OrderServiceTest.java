package jpabook.jpashop.serivce;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.items.Book;
import jpabook.jpashop.domain.items.Item;
import jpabook.jpashop.domain.items.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class OrderServiceTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  OrderService orderService;
  @Autowired
  OrderRepository orderRepository;

  @Test
  void 상품주문() throws Exception {

    Member member = createMember();
    Item item = createBook("AAA JPA", 10000, 10);
    int orderCount = 2;

    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

    Order getOrder = orderRepository.findOne(orderId);

    assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
    assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
    assertEquals(10000 * 2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
    assertEquals(8, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
  }

  private Item createBook(String name, int price, int stockQuantity) {
    Book book = new Book();
    book.setName(name);
    book.setStockQuantity(stockQuantity);
    book.setPrice(price);
    em.persist(book);
    return book;
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "가가", "123-123"));
    em.persist(member);
    return member;
  }

  @Test
  void 상품주문_재고수량초과() {

    Member member = createMember();
    Item item = createBook("JPA AA", 10000, 10);

    int orderCount = 11;

    assertThatThrownBy(() -> {
      orderService.order(member.getId(), item.getId(), orderCount);
    }).isInstanceOf(NotEnoughStockException.class);
//    fail("재고 수량 부족 예외가 발생해야 한다.");
  }

  @Test
  void 주문취소() {
    Member member = createMember();
    Item item = createBook("JPA AAA", 10000, 10);
    int orderCount = 2;

    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

    orderService.cancelOrder(orderId);

    Order getOrder = orderRepository.findOne(orderId);

    assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
    assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가");

  }

}