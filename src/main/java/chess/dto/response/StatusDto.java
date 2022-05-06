package chess.dto.response;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatusDto {

    private static final int COLOR_COUNT = 2;

    private final Map<String, Double> score;
    private final String winner;

    public StatusDto(Map<String, Double> score) {
        this.score = score;
        this.winner = findWinner(score);
    }

    private String findWinner(Map<String, Double> score) {
        final List<String> maxScoreColors = findMaxScoreColors(score, findMaxScore(score));
        if (maxScoreColors.size() == COLOR_COUNT) {
            return "무승부";
        }
        return maxScoreColors.get(0);
    }

    private List<String> findMaxScoreColors(Map<String, Double> score, Double maxScore) {
        return score.keySet().stream()
                .filter(s -> Objects.equals(score.get(s), maxScore))
                .collect(Collectors.toList());
    }

    private double findMaxScore(Map<String, Double> score) {
        return score.values().stream()
                .mapToDouble(point -> point)
                .max()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Map<String, Double> getScore() {
        return score;
    }

    public String getWinner() {
        return winner;
    }
}
