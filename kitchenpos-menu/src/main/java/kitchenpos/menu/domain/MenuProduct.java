package kitchenpos.menu.domain;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kitchenpos.common.domain.Price;
import kitchenpos.common.domain.Quantity;
import kitchenpos.product.domain.Product;

@Entity
public class MenuProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private Quantity quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    protected MenuProduct() {}

    public MenuProduct(Long seq, Quantity quantity, Menu menu, Product product) {
        this.seq = seq;
        this.quantity = quantity;
        this.menu = menu;
        this.product = product;
    }

    public MenuProduct(Quantity quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    private MenuProduct(Long seq, Menu menu, Product product, Long quantity) {
        this(menu, quantity, product);
        this.seq = seq;
    }

    public MenuProduct(Menu menu, Long quantity, Product product) {
        this.menu = menu;
        this.quantity = new Quantity(quantity);
        this.product = product;
    }

    public static MenuProduct of(Product product, Long quantity) {
        return new MenuProduct(null, null, product, quantity);
    }

    public Price calculatePrice() {
        Price price = product.getPrice();
        return price.multiply(BigDecimal.valueOf(quantity.value()));
    }

    public void updateMenu(Menu menu) {
        if (this.menu != menu) {
            this.menu = menu;
            menu.addMenuProduct(this);
        }
    }

    public Long getSeq() {
        return seq;
    }

    public Product getProduct() {
        return product;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(final Menu menu) {
        this.menu = menu;
    }
}
