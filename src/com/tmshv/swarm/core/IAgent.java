package com.tmshv.swarm.core;

import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 15/07/16
 *
 * @author tmshv
 */
public interface IAgent {
    public void run();

    public boolean isMoving();

    public void interact(IAgent other);

    public void interact(Attractor attractor);

    public PVector getLocation();

    public int getLifetime();

    public void setLifetime(int value);

    public String getType();

    public Track getTrack();

    public int getColor();

    public ArrayList<Attractor> getCurrentAttractors();

    public float getInterestDistance();

    public void setInterestDistance(float value);

    public float getMass();

    public void setMass(float value);
}
