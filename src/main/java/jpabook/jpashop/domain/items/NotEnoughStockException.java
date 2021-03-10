package jpabook.jpashop.domain.items;

public class NotEnoughStockException extends RuntimeException {

  public NotEnoughStockException(String need_more_stock) {
    super(need_more_stock);
  }

  public NotEnoughStockException() {
  }

  public NotEnoughStockException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotEnoughStockException(Throwable cause) {
    super(cause);
  }
}
