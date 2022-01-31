package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final String NO_FIRST_STATION_MESSAGE = "상행 종점역이 존재하지 않습니다.";
    private static final int LAST_INDEX_VALUE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        if (isMiddleSection(section)) {
            addMiddleSection(section);
            return;
        }

        sectionList.add(section);
    }

    private boolean isMiddleSection(Section section) {
        return sectionList.stream()
                .anyMatch(otherSection ->
                        section.getUpStation().equals(otherSection.getUpStation())
                                || section.getDownStation().equals(otherSection.getDownStation()));
    }

    private void addMiddleSection(Section section) {
        for (Section otherSection : sectionList) {
            if (section.getUpStation().equals(otherSection.getUpStation())) {
                otherSection.changeUpStation(section.getDownStation());
                break;
            }

            if (section.getDownStation().equals(otherSection.getDownStation())) {
                otherSection.changeDownStation(section.getUpStation());
                break;
            }
        }
        sectionList.add(section);
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public List<Station> getStationList() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        Section firstSection = findFirstSection();
        List<Station> stations = makeStationList(firstSection);

        return Collections.unmodifiableList(stations);
    }

    private List<Station> makeStationList(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        while (true) {
            Section finalSection = section;
            Optional<Section> nextSection = sectionList.stream()
                    .filter(otherSection -> finalSection.getDownStation().equals(otherSection.getUpStation()))
                    .findAny();

            if (nextSection.isPresent()) {
                stations.add(nextSection.get().getUpStation());
                section = nextSection.get();
                continue;
            }

            break;
        }

        stations.add(section.getDownStation());
        return stations;
    }

    private Section findFirstSection() {
        for (Section section : sectionList) {
            if (sectionList.stream()
                    .noneMatch(otherSection -> section.getUpStation().equals(otherSection.getDownStation()))) {
                return section;
            }
        }
        throw new IllegalArgumentException(NO_FIRST_STATION_MESSAGE);
    }

    public void deleteSection(Station station) {
        if (!lastStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sectionList.remove(lastIndex());
    }

    private Station lastStation() {
        return sectionList.get(lastIndex()).getDownStation();
    }

    private int lastIndex() {
        return sectionList.size() - LAST_INDEX_VALUE;
    }
}
