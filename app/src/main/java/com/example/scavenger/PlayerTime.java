package com.example.scavenger;

public class PlayerTime {
    private String playerId, huntId;
    private long time;

    /**
     * Constructor
     * @param playerId Id of player
     * @param huntId Id of hunt
     * @param time Time player completed the hunt
     */
    PlayerTime(String playerId, String huntId, long time) {
        this.playerId = playerId;
        this.huntId = huntId;
        this.time = time;
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
    public long getTime() { return time; }
}
