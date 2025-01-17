package kitchenpos.menu.dto;

import java.util.List;
import kitchenpos.common.domain.Quantity;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.product.domain.Product;

public class MenuProductRequest {
    private Long productId;
    private Long quantity;

    protected MenuProductRequest() {}

    private MenuProductRequest(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static MenuProductRequest of(Long productId, Long quantity) {
        return new MenuProductRequest(productId, quantity);
    }

    public static MenuProductRequest from(MenuProduct menuProduct) {
        return new MenuProductRequest(menuProduct.getProduct().getId(), menuProduct.getQuantity().value());
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public MenuProduct createMenuProduct(List<Product> products) {
        Product product = findProduct(products);
        return new MenuProduct(new Quantity(quantity), product);
    }

    private Product findProduct(List<Product> products) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .get();
    }

    public MenuProduct toMenuProducts(Menu menu, List<Product> products) {
        Product target = findProductByProductId(products);
        return new MenuProduct(menu, quantity, target);
    }

    private Product findProductByProductId(List<Product> products) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst().get();
    }
}
