package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.AddSectionFailException;
import nextstep.subway.exception.RemoveSectionFailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Line _5호선;
    private Station 군자역;
    private Station 아차산역;
    private Station 광나루역;
    private Station 천호역;
    private int distance;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setup() {
        // given
        sections = new Sections();
        _5호선 = new Line("5호선", "파란색");
        군자역 = new Station(1L, "군자역");
        아차산역 = new Station(2L, "아차산역");
        광나루역 = new Station(3L, "광나루역");
        천호역 = new Station(4L, "천호역");
        distance = 10;
        section = Section.of(_5호선, 군자역, 아차산역, distance);
        sections.addSection(section);
    }

    @DisplayName("새로운 역을 구간의 중간에 추가")
    @Test
    void addSectionBetweenSection() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(newSection, section);
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(newSectionDistance);
        assertThat(sections.getSections().get(1).getDistance()).isEqualTo(distance - newSectionDistance);
    }

    @DisplayName("새로운 역을 상행 종점으로 추가")
    @Test
    void addSectionInFrontSection() {
        Section newSection = Section.of(_5호선, 광나루역, 군자역, distance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(newSection, section);
    }

    @DisplayName("새로운 역을 하행 종점으로 추가")
    @Test
    void addSectionInBackSection() {
        Section newSection = Section.of(_5호선, 아차산역, 광나루역, distance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(section, newSection);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    @Test
    void addSectionFailInvalidDistance() {
        int newSectionDistance = 11;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(AddSectionFailException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addSectionFailDuplicatedStations() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 아차산역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(AddSectionFailException.class);
    }

    @DisplayName("상행역과 하행역이 노선에 모두 등록되어 있지 않다면 추가할 수 없다")
    @Test
    void addSectionFailNotContainsStations() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 광나루역, 천호역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(AddSectionFailException.class);
    }

    @DisplayName("역 목록 조회시 등록된 역을 구간 순서대로 반환한다")
    @Test
    void getStations() {
        Section newSection = Section.of(_5호선, 군자역, 광나루역, 3);
        sections.addSection(newSection);

        assertThat(sections.getStations()).containsExactly(군자역, 광나루역, 아차산역);
    }

    @DisplayName("상행 종점 역 삭제")
    @Test
    void deleteFirstStation() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 광나루역, 군자역, newSectionDistance);
        sections.addSection(newSection);
        sections.removeSection(광나루역);

        assertThat(sections.getStations()).containsExactly(군자역, 아차산역);
        Section section = sections.getSections().get(0);
        assertThat(section.getUpStation()).isEqualTo(군자역);
        assertThat(section.getDownStation()).isEqualTo(아차산역);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @DisplayName("하행 종점 역 삭제")
    @Test
    void deleteLastStation() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 아차산역, 광나루역, newSectionDistance);
        sections.addSection(newSection);
        sections.removeSection(광나루역);

        assertThat(sections.getStations()).containsExactly(군자역, 아차산역);
        Section section = sections.getSections().get(0);
        assertThat(section.getUpStation()).isEqualTo(군자역);
        assertThat(section.getDownStation()).isEqualTo(아차산역);
    }

    @DisplayName("중간 역 삭제")
    @Test
    void deleteBetweenStation() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);
        sections.addSection(newSection);
        sections.removeSection(광나루역);

        assertThat(sections.getStations()).containsExactly(군자역, 아차산역);
        Section section = sections.getSections().get(0);
        assertThat(section.getUpStation()).isEqualTo(군자역);
        assertThat(section.getDownStation()).isEqualTo(아차산역);
        assertThat(section.getDistance()).isEqualTo(distance);
    }


    @DisplayName("마지막 구간에 속한 역은 삭제 불가")
    @Test
    void deleteLastSection() {
        assertThatThrownBy(() -> sections.removeSection(군자역))
                .isInstanceOf(RemoveSectionFailException.class);
    }

    @DisplayName("구간에 속하지 않은 역은 삭제 불가")
    @Test
    void deleteNotContainsStation() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);
        sections.addSection(newSection);

        assertThatThrownBy(() -> sections.removeSection(천호역))
                .isInstanceOf(RemoveSectionFailException.class);
    }
}