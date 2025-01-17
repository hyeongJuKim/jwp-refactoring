package kitchenpos.menugroup.acceptance;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.common.AcceptanceTest;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.menugroup.dto.MenuGroupRequest;
import kitchenpos.menugroup.dto.MenuGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("메뉴 그룹 관련 인수 테스트")
public
class MenuGroupAcceptanceTest extends AcceptanceTest {
    private MenuGroupRequest 한마리메뉴_요청;
    private MenuGroupRequest 두마리메뉴_요청;
    private MenuGroupResponse 한마리메뉴_생성됨;
    private MenuGroupResponse 두마리메뉴_생성됨;

    @BeforeEach
    public void setUp() {
        super.setUp();
        한마리메뉴_요청 = MenuGroupRequest.of("한마리메뉴");
        두마리메뉴_요청 = MenuGroupRequest.of("두마리메뉴");
    }

    @Test
    void 메뉴_그룹을_등록할_수_있다() {
        // when
        ExtractableResponse<Response> response = 메뉴그룹_생성_요청(한마리메뉴_요청);

        // then
        메뉴_그룹_생성됨(response);
    }

    @Test
    void 메뉴_그룹_목록을_조회할_수_있다() {
        // given
        한마리메뉴_생성됨 = 메뉴그룹_생성_요청(한마리메뉴_요청).as(MenuGroupResponse.class);
        두마리메뉴_생성됨 = 메뉴그룹_생성_요청(두마리메뉴_요청).as(MenuGroupResponse.class);

        // when
        ExtractableResponse<Response> response = 메뉴그룹_목록_조회_요청();

        // then
        메뉴그룹_목록_응답됨(response, Arrays.asList(한마리메뉴_생성됨.getId(), 두마리메뉴_생성됨.getId()));

    }

    public static ExtractableResponse<Response> 메뉴그룹_생성_요청(MenuGroupRequest request) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 메뉴그룹_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/menu-groups")
                .then().log().all()
                .extract();
    }

    private void 메뉴_그룹_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 메뉴그룹_목록_응답됨(ExtractableResponse<Response> response, List<Long> menuGroupIds) {
        List<Long> ids = response.jsonPath().getList(".", MenuGroup.class)
                        .stream()
                        .map(MenuGroup::getId)
                        .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(ids).containsAll(menuGroupIds)
        );
    }
}
