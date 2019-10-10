package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "M")
@Getter @Setter
public class Movie extends Item {

    private String director;
    private String actor;
}
