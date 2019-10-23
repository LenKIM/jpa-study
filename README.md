인프런 강의

# 실전! 스프링부트-JPA-활용 1



## 단축키

**멀티라인 선택하는 방법**

Option+2번 누른 상태에서 상하

**테스트 클래스 생성**

Command+Ctrl+T

**대소문자 변경**

Command+Shift+U

**추천 완성**

Ctrl+Space



## 알게 된 내용

- 도메인 객체의 Id는 도메인 이름을 붙인다. ex) member객체라면 "member_id" 이런 형식으로
- 연관관계의 주인을 설정해주는 연관관계 메서드를 만들어 준다. 이때 연관관계의 주인은 외래 키가 있는 곳으로 정해야 한다.

```java
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // FK가 member_id가 된다. 주인으로 정한다.
    private Member member;
  
  	...
      
    //==연관관계 메서드== 컨트롤하는 쪽이 들고 있는게 좋다.//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
}

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member") // 나는 주인이 아니다. 읽기 전용이 되는 것이다.
    private List<Order> orders = new ArrayList();
  
  	...
}
```

- 모든 맵핑관계는 지연타입으로 설정해야 한다. 만약 지연타입으로 문제가 생기면 해결해야 한다. 이렇게 하는 지유는 즉시로딩은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행 할 때 N+1문제가 발생한다. 특히, @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시 로딩이므로 직접 지연로딩으로 설정해야 한다.

- 생성메서드를 만드는 습관을 갖자.

```java
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private long id;

  ...

    //== 생성 메서드 --//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
```

- 엔티티에서 롬복을 사용할 시, Getter, Setter 모두 사용하지 않고, Setter 사용을 가급적이면 막아야 한다.
- 실무에서는 @ManyToMany를 사용하지 말자. 만약 사용해야 한다면, 중간에 맵핑 테이블을 하나 만들어 문제를 풀어내자.
- 값 타입은 변경 불가능하게 설계해야 한다.

```java
@Embeddable
@Getter
public class Address {
  private String city;
  private String street;
  private String zipcode;
  protected Address() {
  }
  public Address(String city, String street, String zipcode) {
    this.city = city;
    this.street = street;
    this.zipcode = zipcode;
  } 
}
```

- 기본생성자는 Pulibc 또는 Protected로 놓는 것이 JPA구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있을 정도의 수준으로 놓아야 하기 때문이다.

- 엔티티에서 사용되는 컬렉션은 필드에서 초기화 해야한다.
- 테이블, 컬럼명 생성 전략 - 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다르다. 그러므로, 설정해주어야 한다.

- 위에서 언급한 것과 같이 맵핑 관계를 고려해 도메인을 구현할 때는, 연관관계 메서드 / 생성 메서드 / 비지니스 로직 메서드를 나눠서 작업하는 것이 좋다.

```java
package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

		...
      
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status; // 주문 상태

    //==연관관계 메서드== 컨트롤하는 쪽이 들고 있는게 좋다.//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
//    TODO 양방향일 때 쓰면 좋다. 왜??

    //== 생성 메서드 --//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		...
    }

    //비지니스 로직

    /**
     * 주문 취소
     */
    public void cancel() {
      ...
    }

    /**
     * 조회로직
     */

    public int getTotalPrice() {
			...
    }
}

```

