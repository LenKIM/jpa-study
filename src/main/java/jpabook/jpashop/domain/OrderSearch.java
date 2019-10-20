package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSearch {

    OrderStatus orderStatus;
    String memberName;
}
