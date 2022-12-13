package kitchenpos.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import kitchenpos.product.application.ProductService;
import kitchenpos.product.dao.ProductDao;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    private Product 양념치킨;
    private Product 후라이드치킨;

    @BeforeEach
    void setUp() {
        양념치킨 = new Product(1L, "양념치킨", BigDecimal.valueOf(2_000));
        후라이드치킨 = new Product(1L, "후라이드치킨", BigDecimal.valueOf(18_000));
    }

    @Test
    void 상품을_등록_할_수_있다() {
        given(productDao.save(any())).willReturn(양념치킨);

        Product savedProduct = productService.create(양념치킨);

        assertAll(
                () -> assertThat(savedProduct.getId()).isNotNull(),
                () -> assertThat(savedProduct.getName()).isEqualTo(양념치킨.getName()),
                () -> assertThat(savedProduct.getPrice()).isEqualTo(양념치킨.getPrice())
        );
    }

    @Test
    void 가격이_존재하지_않는_상품은_등록할_수_없다() {
        Product product = new Product(1L, "반반치킨", null);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1})
    void 상품의_가격은_0원_이상이어야_한다(int price) {
        Product product = new Product(1L, "반반치킨", BigDecimal.valueOf(price));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_목록을_조회할_수_있다() {
        given(productDao.findAll()).willReturn(Arrays.asList(양념치킨, 후라이드치킨));

        List<Product> products = productService.list();

        assertThat(products).hasSize(2);
        assertThat(products).contains(양념치킨, 후라이드치킨);
    }
}