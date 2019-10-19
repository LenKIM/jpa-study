package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "B")
@Getter @Setter
public class Book extends Item {

    private String author;
    private String isbn;
    public Book() {
    }
    public Book(String author, String isbn) {
        this.author = author;
        this.isbn = isbn;
    }


}
