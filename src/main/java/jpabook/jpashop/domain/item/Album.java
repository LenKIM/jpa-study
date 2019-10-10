package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "A")
@Getter @Setter
public class Album extends Item {

    private String artiest;
    private String etc;
}
