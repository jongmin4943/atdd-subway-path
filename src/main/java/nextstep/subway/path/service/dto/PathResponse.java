package nextstep.subway.path.service.dto;

import nextstep.subway.path.domain.Path;
import nextstep.subway.station.service.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private int distance;
    private List<StationResponse> stations;

    public PathResponse() {
    }

    public PathResponse(final int distance, final List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public static PathResponse of(final Path path) {
        return new PathResponse(path.getDistance(), path.getStations().stream().map(StationResponse::from).collect(Collectors.toList()));
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
