package com.teabreaktechnology.dumcharades.bean;

import java.io.Serializable;

/**
 * Created by kishorekpendyala on 1/18/15.
 */
public class GamePlay implements Serializable {
    private int gameId;
    private int roundId;
    private int teamId;
    private int playerId;
    private int movieId;
    private int score;

    public GamePlay(Builder builder) {
        this.playerId = builder.playerId;
        this.gameId = builder.gameId;
        this.teamId = builder.teamId;
        this.movieId = builder.teamId;
        this.score = builder.score;
        this.roundId = builder.roundId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("{ gameId :").append(gameId).append("}");
        sb.append("{ roundId :").append(roundId).append("}");
        sb.append("{ teamId :").append(teamId).append("}");
        sb.append("{ playerId :").append(playerId).append("}");
        sb.append("{ movieId :").append(movieId).append("}");
        sb.append("{ score :").append(score).append("}");
        sb.append("}");
        return sb.toString();
    }

    public int getGameId() {
        return gameId;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamePlay gamePlay = (GamePlay) o;

        if (gameId != gamePlay.gameId) return false;
        return roundId == gamePlay.roundId;

    }

    @Override
    public int hashCode() {
        int result = gameId;
        result = 31 * result + roundId;
        return result;
    }

    public static class Builder {

        private int gameId;
        private int roundId;
        private int teamId;
        private int playerId;
        private int movieId;
        private int score;

        public Builder playerId(int playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder roundId(int roundId) {
            this.roundId = roundId;
            return this;
        }

        public Builder gameId(int gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder movieId(int movieId) {
            this.movieId = movieId;
            return this;
        }

        public Builder teamId(int teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public GamePlay build() {
            return new GamePlay(this);
        }
    }
}
