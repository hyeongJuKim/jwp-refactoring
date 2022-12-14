package kitchenpos.ordertable.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.repository.OrderRepository;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.dto.OrderTableRequest;
import kitchenpos.ordertable.dto.OrderTableResponse;
import kitchenpos.ordertable.dto.UpdateEmptyRequest;
import kitchenpos.ordertable.dto.UpdateNumberOfGuestsRequest;
import kitchenpos.ordertable.repository.OrderTableRepository;
import kitchenpos.tablegroup.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문 테이블 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class TableServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @InjectMocks
    private TableService tableService;

    private OrderTable firstTable;
    private OrderTable secondTable;
    private TableGroup 개발자_단체;

    @BeforeEach
    void setUp() {
        firstTable = new OrderTable(0, true);
        secondTable = new OrderTable(0, true);
        개발자_단체 = new TableGroup(1L, null, Arrays.asList(firstTable, secondTable));
    }

    @Test
    void 주문_테이블을_등록할_수_있다() {
        given(orderTableRepository.save(firstTable)).willReturn(firstTable);

        OrderTableResponse savedOrderTable = tableService.create(
                OrderTableRequest.of(firstTable.getNumberOfGuests(), firstTable.isEmpty())
        );

        assertAll(
                () -> assertThat(savedOrderTable.getId()).isEqualTo(firstTable.getId()),
                () -> assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(firstTable.getNumberOfGuests())
        );
    }

    @Test
    void 주문_테이블_목록을_조회할_수_있다() {
        given(orderTableRepository.findAll()).willReturn(Arrays.asList(firstTable, secondTable));

        List<OrderTableResponse> orderTables = tableService.list();

        assertThat(orderTables).hasSize(2);
        assertThat(orderTables.stream().map(OrderTableResponse::getId))
                .contains(firstTable.getId(), secondTable.getId());
    }

    @Test
    void 주문_테이블_이용_여부를_변경할_수_있다() {
        OrderTable expected = new OrderTable(1L, 1, true);
        UpdateEmptyRequest request = UpdateEmptyRequest.of(false);
        given(orderTableRepository.findById(expected.getId())).willReturn(Optional.of(firstTable));
        given(orderTableRepository.save(firstTable)).willReturn(firstTable);

        OrderTableResponse changeOrderTable = tableService.changeEmpty(expected.getId(), request);

        assertThat(changeOrderTable.isEmpty()).isFalse();
    }

    @Test
    void 단체_테이블에_지정되어_있으면_주문_테이블을_변경할_수_없다() {
        firstTable.setTableGroup(개발자_단체);
        UpdateEmptyRequest request = UpdateEmptyRequest.of(firstTable.isEmpty());
        given(orderTableRepository.findById(firstTable.getId())).willReturn(Optional.of(firstTable));

        assertThatThrownBy(() -> tableService.changeEmpty(firstTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_상태가_조리_또는_식사중이면_테이블_이용_여부를_변경할_수_없다() {
        List<OrderStatus> orderStatus = Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL);
        UpdateEmptyRequest request = UpdateEmptyRequest.of(firstTable.isEmpty());
        given(orderTableRepository.findById(firstTable.getId())).willReturn(Optional.of(firstTable));
        given(orderRepository.existsByOrderTableIdAndOrderStatusIn(firstTable.getId(), orderStatus)).willReturn(true);

        assertThatThrownBy(() -> tableService.changeEmpty(firstTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_변경할_수_있다() {
        OrderTable expected = new OrderTable(1L, 5, false);
        firstTable.setEmpty(false);
        UpdateNumberOfGuestsRequest request = UpdateNumberOfGuestsRequest.of(expected.getNumberOfGuests());
        given(orderTableRepository.findById(expected.getId())).willReturn(Optional.of(firstTable));
        given(orderTableRepository.save(firstTable)).willReturn(firstTable);

        OrderTableResponse changeOrderTable = tableService.changeNumberOfGuests(expected.getId(), request);

        assertThat(changeOrderTable.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @Test
    void 주문_테이블의_손님_수를_음수로_변경할_수_없다() {
        OrderTable expected = new OrderTable(1L, -1, false);
        UpdateNumberOfGuestsRequest request = UpdateNumberOfGuestsRequest.of(-1);

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(expected.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블이_빈_테이블이면_손님_수를_변경할_수_없다() {
        //firstTable.setEmpty(true);
        UpdateNumberOfGuestsRequest request = UpdateNumberOfGuestsRequest.of(0);
        given(orderTableRepository.findById(firstTable.getId())).willReturn(Optional.of(firstTable));

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(firstTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

