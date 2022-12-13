package kitchenpos.menu.acceptance;

import static kitchenpos.menugroup.acceptance.MenuGroupAcceptanceTest.메뉴그룹_생성_요청;
import static kitchenpos.product.acceptance.ProductAcceptanceTest.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.common.AcceptanceTest;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


@DisplayName("메뉴 관련 인수 테스트")
public
class MenuAcceptanceTest extends AcceptanceTest {
    private Product 후라이드치킨;
    private Product 콜라;
    private MenuGroup 치킨;
    private MenuProduct 후라이드치킨상품;
    private MenuProduct 콜라상품;
    private Menu 치킨콜라세트;

    @BeforeEach
    public void setUp() {
        super.setUp();
        후라이드치킨 = 상품_생성_요청(new Product(1L, "후라이드 치킨", BigDecimal.valueOf(18_000))).as(Product.class);
        콜라 = 상품_생성_요청(new Product(2L, "콜라", BigDecimal.valueOf(1_800))).as(Product.class);
        치킨 = 메뉴그룹_생성_요청(new MenuGroup(1L, "치킨")).as(MenuGroup.class);

        치킨콜라세트 = new Menu(1L, "치킨콜라 세트", BigDecimal.valueOf(19_800), 치킨.getId(), new ArrayList<>());
        후라이드치킨상품 = new MenuProduct(1L, 치킨콜라세트.getId(), 후라이드치킨.getId(), 1L);
        콜라상품 = new MenuProduct(2L, 치킨콜라세트.getId(), 콜라.getId(), 1L);
        치킨콜라세트.setMenuProducts(Arrays.asList(후라이드치킨상품, 콜라상품));
    }

    @Test
    void 매뉴를_등록할_수_있다() {
        // when
        ExtractableResponse<Response> response = 메뉴_생성_요청(치킨콜라세트);

        // then
        메뉴_생성됨(response);
    }

    @Test
    void 메뉴_목록을_조회한다() {
        // given
        치킨콜라세트 = 메뉴_생성_요청(치킨콜라세트).as(Menu.class);

        // when
        ExtractableResponse<Response> response = 메뉴_목록_조회_요청();

        // then
        메뉴_목록_응답됨(response, Arrays.asList(치킨콜라세트.getId()));
    }

    public static ExtractableResponse<Response> 메뉴_생성_요청(Menu menu) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menu)
                .when().post("/api/menus")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 메뉴_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/menus")
                .then().log().all()
                .extract();
    }

    private void 메뉴_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 메뉴_목록_응답됨(ExtractableResponse<Response> response, List<Long> menuIds) {
        List<Long> ids = response.jsonPath().getList(".", Menu.class)
                        .stream()
                        .map(Menu::getId)
                        .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(ids).containsAll(menuIds)
        );
    }
}