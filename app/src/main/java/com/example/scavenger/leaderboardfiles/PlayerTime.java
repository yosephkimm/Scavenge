package com.example.scavenger.leaderboardfiles;

public class PlayerTime {
    private String playerId, huntId;
    private double time;

    /**
     * Constructor
     * @param playerId Id of player
     * @param huntId Id of hunt
     * @param time Time player completed the hunt
     */
    public PlayerTime(String playerId, String huntId, double time) {
        this.playerId = playerId;
        this.huntId = huntId;
        this.time = time;
    }

    PlayerTime() {
        playerId = "";
        huntId = "";
    }

    /**
     * Getter for playerId
     * @return Id of player
     */
    public String getPlayerId() { return playerId; }

    /**
     * Getter for huntId
     * @return Id of hunt
     */
    public String getHuntId() { return huntId; }

    /**
     * Getter for time
     * @return Time player completed the hunt
     */
    public double getTime() { return time; }
}
