# 요구사항



![image-20210307133116822](https://tva1.sinaimg.cn/large/008eGmZEgy1gob9sjx2jxj310o0u041m.jpg)

# 기능 목록

- 회원 기능 
  - 회원 등록 
  - 회원 조회 
- 상품 기능 
  - 상품 등록 
  - 상품 수정 
  - 상품 조회 
- 주문 기능 
  - 상품 주문 
  - 주문 내역 조회
  -  주문 취소 
- 기타 요구사항 
  - 상품은 재고 관리가 필요하다.
  - 상품의 종류는 도서, 음반, 영화가 있다. 
  - 상품을 카테고리로 구분할 수 있다.
  - 상품 주문시 배송 정보를 입력할 수 있다.

![image-20210307133240060](https://tva1.sinaimg.cn/large/008eGmZEgy1gob9smjqtbj31220j6q56.jpg)

## 회원, 주문, 상품의 관계

 회원은 여러 상품을 주문할 수 있다. 그리고 한 번 주문할 때 여러 상품을 선택할 수 있으므로 주문과 상품은 다대다 관계다. 하지만 이런 다대다 관계는 관계형 데이터베이스는 물론이고 엔티 티에서도 거의 사용하지 않는다. 따라서 그림처럼 **주문상품**이라는 엔티티를 추가해서 다대다 관계를 일대 다, 다대일 관계로 풀어냈다.

## 상품 분류

 상품은 도서, 음반, 영화로 구분되는데 상품이라는 공통 속성을 사용하므로 상속 구조로 표현했다.



# 회원 엔티티 분석

![image-20210307133415530](https://tva1.sinaimg.cn/large/008eGmZEgy1gob8gareh7j312y0qgter.jpg)

**회원(Member)**

이름과 임베디드 타입인 주소(`Address`), 그리고 주문(`orders`) 리스트를 가진다.

**주문(Order)** 

한 번 주문시 여러 상품을 주문할 수 있으므로 주문과 주문상품(`OrderItem` )은 일대다 관계다. 주문은 상품을 주문한 회원과 배송 정보, 주문 날짜, 주문 상태(`status` )를 가지고 있다. 주문 상태는 열 거형을 사용했는데 주문( `ORDER` ), 취소( `CANCEL` )을 표현할 수 있다.

**주문상품(OrderItem)**

주문한 상품 정보와 주문 금액(`orderPrice`) 주문 수향(`count`) 정보를 가지고 있다. (보통 `OrderLine` , `LineItem` 으로 많이 표현한다.)

**상품(Item)** 

이름, 가격, 재고수량(`stockQuantity`)을 가지고 있다. 상품을 주문하면 재고수량이 줄어든다. 상품의 종류로는 도서, 음반, 영화가 있는데 각각은 사용하는 속성이 조금씩 다르다.

**배송(Delivery)**

주문시 하나의 배송 정보를 생성한다. 주문과 배송은 일대일 관계다.

**카테고리(Category)**

 상품과 다대다 관계를 맺는다. `parent`,` child`로 부모, 자식 카테고리를 연결한다.

**주소(Address)**

값 타입(임베디드 타입)이다. 회원과 배송(Delivery)에서 사용한다.

> 참고: 회원 엔티티 분석 그림에서 Order와 Delivery가 단방향 관계로 잘못 그려져 있다. 양방향 관계가 맞다.



![image-20210307134001780](https://tva1.sinaimg.cn/large/008eGmZEgy1gob8mhbclej30x80u0tf7.jpg)

**MEMBER**: 회원 엔티티의 Address 임베디드 타입 정보가 회원 테이블에 그대로 들어갔다. 이것은 DELIVERY 테이블도 마찬가지다.

**ITEM**: 앨범, 도서, 영화 타입을 통합해서 하나의 테이블로 만들었다. DTYPE 컬럼으로 타입을 구분한다.

> 참고: 테이블명이 ORDER 가 아니라 ORDERS 인 것은 데이터베이스가  order by 때문에 예약어로 잡고 있는 경우가 많다. 그래서 관례상 ORDERS 를 많이 사용한다.



![image-20210307140319642](https://tva1.sinaimg.cn/large/008eGmZEgy1gob9ajck99j312o0madki.jpg)





> 참고: 외래 키가 있는 곳을 연관관계의 주인으로 정해라.
>
> 연관관계의 주인은 단순히 외래 키를 누가 관리하냐의 문제이지 비즈니스상 우위에 있다고 주인으로 정하면 안된다.. 예를 들어서 자동차와 바퀴가 있으면, 일대다 관계에서 항상 다쪽에 외래 키가 있으므로 외래 키가 있는 바퀴를 연관관계의 주인으로 정하면 된다. 물론 자동차를 연관관계의 주인으로 정하는 것이 불가능 한 것은 아니지만, 자동차를 연관관계의 주인으로 정하면 자동차가 관리하지 않는 바퀴 테이블의 외래 키 값이 업데이트 되므로 관리와 유지보수가 어렵고, 추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제도 있다.



---

[학습 노트]

왜 멤버가 팀을 선택해야 하는가?

팀이 멤버를 선택하면 안되는가?

```java
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  private String name;

  @Embedded
  private Address address;

  @OneToMany(mappedBy = "member") // 외래키가 있는 쪽에 mappedBy 설정
  private List<Order> orders = new ArrayList<>();
}

```

```java

@Entity(name = "orders")
@Getter
@Setter
public class Order {

  @Id
  @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
  
}
```



Order와 Member의 연관관계는 양방향이다. 어떻게 아는가? `Order`와 `Member`가 모두 `@ManyToOne`,`@OneToMany` 를 가지고 있으며, ` @JoinColumn(name="member_id")` 가 FK를 설정한다.

이 때, mappedBy의 정체를 이해하자. 

member_id를 외래 키로 동작되어 양방향 조회가 가능하다. 만약 Member의 Order 를 알고 싶다면, member_id로 Member에 조인해서 조회가 가능하다.

![image-20210308095033578](https://tva1.sinaimg.cn/large/008eGmZEgy1goc7lwpo3hj311u0emmy6.jpg)

위는 데이터베이스 관점에서 모델링된 형태이고, 아래는 객체지향 프로그래밍에서 객체간의 관계이다.

![image-20210308095728199](https://tva1.sinaimg.cn/large/008eGmZEgy1goc7t13kedj310o0u0416.jpg)



객체의 연관관계는 사실 두 개의 단방향으로 존재.

엔티티의 양방향 매핑으로 설정하면 객체의 참조는 둘인데 반해, 테이블은 하나의 외래 키만을 사용.

양쪽에서 접근가능하여 데이터를 변경할 수 있지만, JPA가 만든 규칙은 다음과 같다.

한쪽에서만 테이블의 외래키(FK)를 바꿀 수 있도록 하는 것이다. 그 관리자를 "연관관계의 주인"이라 정한다.

MappedBy는 어떤 객체가 주인인지를 표시하는 어노테이션으로, 위 코드에서 `mappedBy(member)` 라면, mebmer를 갖고 있는 **Order가 Member[FK] 의 주인이 되는 것이다.**

>\- 오직 주인만 FK를 관리한다.
>
> - 주인이 아닌 곳에서는 mappedBy로 주인을 명시해야한다.

즉, FK가 있는 곳을 주인으로 한다.



***그런데 바로, 아래 OrderItem 의 코드를 작성할 때는 위와같이 하지 않았다. 왜?***

나처럼 생각한 사람이 바로 있었다. https://www.inflearn.com/questions/39523

![image-20210308153403182](https://tva1.sinaimg.cn/large/008eGmZEgy1gochjagv0jj30p10cxtb0.jpg)

>  연관관계의 주인이라는 말의 뜻은, 단순히 외래 키를 누가 관리하냐의 문제이지 비즈니스상 우위에 있다고 주인으로 정하는 것이 아니다. 예를 들어서 자동차와 바퀴가 있으면, 일대다 관계에서 항상 다쪽에 외래 키가 있으므로 외래 키가 있는 바퀴를 연관관계의 주인으로 정하면 된다. 물론 자동차를 연관관계의 주인으로 정하는 것이 불가능 한 것은 아니지만, 자동차를 연관관계의 주인으로 정하면 자동차가 관리하지 않는 바퀴 테이블의 외래 키 값이 업데이트 되므로 관리와 유지보수가 어렵고, 추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제도 있 다. 자세한 내용은 JPA 기본편을 참고하자.
>
> \- 김영한의 "실전, 스프링 부트와 JPA활용1 중..."



---

![image-20210308155840742](https://tva1.sinaimg.cn/large/008eGmZEgy1goci8vkyvyj31gm0lagqb.jpg)



`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` 의미는 무엇인가?





---

