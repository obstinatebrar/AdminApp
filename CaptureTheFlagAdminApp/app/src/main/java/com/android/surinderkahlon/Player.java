package com.android.surinderkahlon;

public class Player {
    String playerName;
    String playerTeam;
    Double latitude;
    Double longitude;
    public Player(){

    }
    public Player(String playerName, String playerTeam){
        this.playerName = playerName;
        this.playerTeam = playerTeam;
    }
    public Player(String playerName, String playerTeam, Double latitude, Double longitude){
        this.playerName = playerName;
        this.playerTeam = playerTeam;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
