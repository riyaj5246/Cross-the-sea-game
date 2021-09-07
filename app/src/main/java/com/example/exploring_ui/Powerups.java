package com.example.exploring_ui;

public class Powerups {

    private int type_power;
    private String name;
    private boolean enabled = true;
    private int turns_enabled = 0;

    public Powerups(int type){
        type_power = type;
        switch(type_power){
            case 0:
                name = "freeze";
                break;
            case 1:
                name = "speed";
                break;
            case 2:
                name = "portal";
        }
    }

    public int getType(){
        return type_power;
    }

    public String getName(){
        return name;
    }

    public boolean getEnabled(){
        return enabled;
    }

    public void addToTurnsEnabled(){
        turns_enabled += 1;
    }

    public int getTurnsEnabled(){
        return turns_enabled;
    }

    public void disable(){
        enabled = false;
    }
}
