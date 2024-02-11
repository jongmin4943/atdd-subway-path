package nextstep.subway.path.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.service.LineProvider;
import nextstep.subway.path.domain.SubwayMap;
import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineProvider lineProvider;

    public PathService(final LineProvider lineProvider) {
        this.lineProvider = lineProvider;
    }

    public PathResponse findShortestPath(final PathSearchRequest searchRequest) {
        searchRequest.validate();

        final List<Line> allLines = lineProvider.getAllLines();
        final Map<Long, Station> stationMap = createStationMapFrom(allLines);
        final Station sourceStation = stationMap.computeIfAbsent(searchRequest.getSource(), throwStationNotFoundException());
        final Station targetStation = stationMap.computeIfAbsent(searchRequest.getTarget(), throwStationNotFoundException());

        final SubwayMap subwayMap = new SubwayMap(allLines);

        return PathResponse.of(subwayMap.findShortestPath(sourceStation, targetStation));
    }

    private Map<Long, Station> createStationMapFrom(final List<Line> allLines) {
        return allLines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toMap(Station::getId, Function.identity()));
    }

    private static Function<Long, Station> throwStationNotFoundException() {
        return id -> {
            throw new StationNotExistException(id);
        };
    }
}
