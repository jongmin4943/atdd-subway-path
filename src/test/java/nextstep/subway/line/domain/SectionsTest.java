package nextstep.subway.line.domain;

import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.line.exception.SectionConnectException;
import nextstep.subway.line.exception.SectionDisconnectException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private Station 교대역;
    private Section 강남역_선릉역_구간;
    private Section 선릉역_역삼역_구간;
    private Sections sections;

    @BeforeEach
    void setUp() {
        강남역 = StationFactory.createStation(1L, "강남역");
        선릉역 = StationFactory.createStation(2L, "선릉역");
        역삼역 = StationFactory.createStation(3L, "선릉역");
        교대역 = StationFactory.createStation(4L, "교대역");
        강남역_선릉역_구간 = SectionFactory.createSection(1L, 강남역, 선릉역, 10);
        선릉역_역삼역_구간 = SectionFactory.createSection(2L, 선릉역, 역삼역, 20);
        sections = new Sections();
        sections.connect(강남역_선릉역_구간);
        sections.connect(선릉역_역삼역_구간);
    }

    @Test
    @DisplayName("마지막 section 의 distance 를 반환받을 수 있다.")
    void getLastSectionDistanceTest() {
        assertThat(sections.getLastSectionDistance()).isEqualTo(선릉역_역삼역_구간.getDistance());
    }

    @Test
    @DisplayName("sections 안의 모든 station 들을 반환받을 수 있다.")
    void getStationsTest() {
        assertThat(sections.getStations()).containsExactly(강남역, 선릉역, 역삼역);
    }

    @Nested
    @DisplayName("Sections connect 테스트")
    class ConnectTest {
        @Test
        @DisplayName("Section 을 추가할 수 있다.")
        void canConnectSection() {
            assertThat(sections).containsExactly(강남역_선릉역_구간, 선릉역_역삼역_구간);
        }

        @Test
        @DisplayName("추가할 Section 의 하행역이 이미 등록되어있으면 SectionConnectException 이 던져진다.")
        void connectFailsWhenSectionsAlreadyHasDownStation() {
            final Section 역삼역_강남역_구간 = SectionFactory.createSection(3L, 역삼역, 강남역, 10);

            assertThatThrownBy(() -> sections.connect(역삼역_강남역_구간))
                    .isInstanceOf(SectionConnectException.class)
                    .hasMessageContaining("생성할 구간 하행역이 해당 노선에 이미 등록되어 있습니다.");
        }

        @Test
        @DisplayName("추가할 Section 의 상행역이 현재 Sections 의 마지막 구간의 하행역과 같지 않다면 SectionConnectException 이 던져진다.")
        void connectFailsWhenSectionsNotConnectable() {
            final Section 선릉역_교대역_구간 = SectionFactory.createSection(3L, 선릉역, 교대역, 10);

            assertThatThrownBy(() -> sections.connect(선릉역_교대역_구간))
                    .isInstanceOf(SectionConnectException.class)
                    .hasMessageContaining("생성할 구간 상행역이 해당 노선의 하행 종점역이 아닙니다.");
        }
    }

    @Nested
    @DisplayName("Sections disconnectLastSection 테스트")
    class disconnectTest {

        @Test
        @DisplayName("마지막 역을 제거할 수 있다.")
        void canDisconnectLastStation() {
            sections.disconnectLastSection(역삼역);

            assertThat(sections).containsExactly(강남역_선릉역_구간);
        }

        @Test
        @DisplayName("Sections 의 길이가 1 이하일때는 SectionDisconnectException 이 던져진다.")
        void disconnectLastSectionFailsWhenLengthIsLoeToOne() {
            sections.disconnectLastSection(역삼역);

            assertThatThrownBy(() -> sections.disconnectLastSection(선릉역))
                    .isInstanceOf(SectionDisconnectException.class)
                    .hasMessageContaining("더이상 구간을 제거할 수 없습니다.");
        }

        @Test
        @DisplayName("마지막 구간이 아닌 Station 을 disconnectLastSection 시 SectionDisconnectException 이 던져진다.")
        void disconnectingStationIsNotDownStationOfLastSection() {
            assertThatThrownBy(() -> sections.disconnectLastSection(선릉역))
                    .isInstanceOf(SectionDisconnectException.class)
                    .hasMessageContaining("마지막 구간만 제거할 수 있습니다.");
        }

    }

}
