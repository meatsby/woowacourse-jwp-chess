package chess.domain.position;

import static chess.domain.position.Column.B;
import static chess.domain.position.Column.C;
import static chess.domain.position.Column.D;
import static chess.domain.position.Column.E;
import static chess.domain.position.Column.F;
import static chess.domain.position.Column.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ColumnTest {

    @Test
    @DisplayName("범위가 넘는 컬럼값은 존재하지 않음")
    void column_mustInAToH() {
        List<String> names = Stream.of(values())
                .map(Enum::name)
                .collect(Collectors.toList());

        assertThat(names).doesNotContain("Z");
    }

    @Test
    @DisplayName("정렬된 전체 컬럼을 가지고 옴")
    void allColumns_sortedByAscending() {
        List<String> orderedNames = Stream.of(values())
                .map(Enum::name)
                .collect(Collectors.toList());

        assertThat(orderedNames).containsExactly("A", "B", "C", "D", "E", "F", "G", "H");
    }

    @Test
    @DisplayName("시작 컬럼과 목표 컬럼 사이에 있는 컬럼들을 반환한다")
    void calculate_columnPaths() {
        assertThat(B.columnPaths(F)).containsExactly(C, D, E);
    }

    @Test
    @DisplayName("시작 컬럼과 목표 컬럼 사이에 있는 컬럼들을 순서에 맞게 반환한다")
    void calculate_columnPathsInRightOrder() {
        assertThat(F.columnPaths(B)).containsExactly(E, D, C);
    }
}
