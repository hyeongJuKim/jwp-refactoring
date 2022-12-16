package kitchenpos.menu.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import kitchenpos.common.domain.Price;

@Embeddable
public class MenuProducts {
    private static final int ZERO = 0;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuProduct> menuProducts = new ArrayList<>();

    protected MenuProducts() {}

   public MenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public List<MenuProduct> getMenuProducts() {
        return Collections.unmodifiableList(menuProducts);
    }

    public Price totalMenuPrice() {
        Price total = new Price(BigDecimal.valueOf(0));
        for (MenuProduct menuProduct : menuProducts) {
            total = total.add(menuProduct.calculatePrice());
        }
        return total;
    }
}
