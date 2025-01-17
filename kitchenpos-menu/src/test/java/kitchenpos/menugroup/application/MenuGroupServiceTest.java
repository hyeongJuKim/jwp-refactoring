package kitchenpos.menugroup.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import kitchenpos.common.domain.Name;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.menugroup.dto.MenuGroupRequest;
import kitchenpos.menugroup.dto.MenuGroupResponse;
import kitchenpos.menugroup.repository.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 그룹 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private MenuGroup 치킨;
    private MenuGroup 스파게티;

    @BeforeEach
    void setUp() {
        치킨 = new MenuGroup(1L, new Name("치킨"));
        스파게티 = new MenuGroup(2L, new Name("스파게티"));
    }

    @Test
    void 메뉴_그룹을_등록할_수_있다() {
        given(menuGroupRepository.save(any())).willReturn(치킨);

        MenuGroupResponse savedProduct = menuGroupService.create(MenuGroupRequest.of(치킨.getName().value()));

        assertAll(
                () -> assertThat(savedProduct.getId()).isNotNull(),
                () -> assertThat(savedProduct.getName()).isEqualTo(치킨.getName().value())
        );
    }


    @Test
    void 메뉴_그룹_목록_조회할_수_있다() {
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(치킨, 스파게티));

        List<MenuGroupResponse> menuGroups = menuGroupService.findAll();

        assertAll(
                () -> assertThat(menuGroups).hasSize(2),
                () -> assertThat(menuGroups.stream().map(MenuGroupResponse::getId))
                        .contains(치킨.getId(), 스파게티.getId())
        );

    }
}
