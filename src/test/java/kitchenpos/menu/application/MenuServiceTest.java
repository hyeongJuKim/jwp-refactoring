package kitchenpos.menu.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.menu.dao.MenuDao;
import kitchenpos.menugroup.dao.MenuGroupDao;
import kitchenpos.menu.dao.MenuProductDao;
import kitchenpos.menu.application.MenuService;
import kitchenpos.product.dao.ProductDao;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuService menuService;

    private Product 치킨;
    private Product 스파게티;
    private MenuProduct 치킨_두마리;
    private MenuProduct 스파게티_이인분;

    @BeforeEach
    void setUp() {
        치킨 = new Product(1L, "치킨", BigDecimal.valueOf(20_000));
        스파게티 = new Product(2L, "스파게티", BigDecimal.valueOf(10_000));
        치킨_두마리 = new MenuProduct(1L, 1L, 1L, 2);
        스파게티_이인분 = new MenuProduct(2L, 1L, 2L, 2);
    }

    @Test
    void 메뉴를_등록할_수_있다() {
        Menu 치킨_스파게티_더블세트_메뉴 = new Menu(1L, "치킨 스파게티 더블세트 메뉴", new BigDecimal(60_000), 1L, Arrays.asList(치킨_두마리,
                스파게티_이인분));
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(1L)).willReturn(Optional.of(치킨));
        given(productDao.findById(2L)).willReturn(Optional.of(스파게티));
        given(menuProductDao.save(치킨_두마리)).willReturn(치킨_두마리);
        given(menuProductDao.save(스파게티_이인분)).willReturn(스파게티_이인분);
        given(menuDao.save(치킨_스파게티_더블세트_메뉴)).willReturn(치킨_스파게티_더블세트_메뉴);

        Menu savedMenu = menuService.create(치킨_스파게티_더블세트_메뉴);

        assertAll(
                () -> assertThat(savedMenu.getMenuProducts()).contains(치킨_두마리, 스파게티_이인분),
                () -> assertThat(savedMenu.getName()).isEqualTo(치킨_스파게티_더블세트_메뉴.getName()),
                () -> assertThat(savedMenu.getPrice()).isEqualTo(치킨_스파게티_더블세트_메뉴.getPrice())
        );
    }

    @Test
    void 가격이_존재하지_않는_메뉴는_등록할_수_없다() {
        Menu 치킨_스파게티_더블세트_메뉴 = new Menu(1L, "치킨 스파게티 더블세트 메뉴", null, 1L, Arrays.asList(치킨_두마리, 스파게티_이인분));

        assertThatThrownBy(() -> menuService.create(치킨_스파게티_더블세트_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 가격이_0원_미만인_메뉴는_등록할_수_없다() {
        Menu 치킨_스파게티_더블세트_메뉴 = new Menu(1L, "치킨 스파게티 더블세트 메뉴", new BigDecimal(-1), 1L, Arrays.asList(치킨_두마리, 스파게티_이인분));

        assertThatThrownBy(() -> menuService.create(치킨_스파게티_더블세트_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_가격은_모든_메뉴_상품의_가격_곱하기_수량의_합보다_작거나_같아야_한다() {
        Menu 치킨_스파게티_더블세트_메뉴 = new Menu(1L, "치킨 스파게티 더블세트 메뉴", new BigDecimal(60_000), 1L, Arrays.asList(치킨_두마리, 스파게티_이인분));
        assertThatThrownBy(() -> menuService.create(치킨_스파게티_더블세트_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메뉴_목록을_조회할_수_있다() {
        Menu 치킨_스파게티_더블세트_메뉴 = new Menu(1L, "치킨 스파게티 더블세트 메뉴", new BigDecimal(60_000), 1L, Arrays.asList(치킨_두마리, 스파게티_이인분));
        Menu 치킨_피자_더블세트_메뉴 = new Menu(1L, "치킨_피자_더블세트_메뉴", new BigDecimal(50_000), 1L, Arrays.asList(치킨_두마리, 스파게티_이인분));
        given(menuDao.findAll()).willReturn(Arrays.asList(치킨_스파게티_더블세트_메뉴, 치킨_피자_더블세트_메뉴));

        List<Menu> menus = menuService.list();

        assertThat(menus).hasSize(2);
        assertThat(menus).contains(치킨_스파게티_더블세트_메뉴, 치킨_피자_더블세트_메뉴);
    }
}