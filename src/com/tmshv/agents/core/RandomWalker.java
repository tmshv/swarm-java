package com.tmshv.agents.core;

import processing.core.PVector;

import java.util.Random;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class RandomWalker extends Agent {
    private Random random = new Random();

    public RandomWalker(String type, float maxForce, float maxSpeed, int agentColor) {
        super(type, maxSpeed, maxForce, agentColor);
    }

    @Override
    public void run() {
        applyRandom();
        super.run();
    }

    private void applyRandom() {
        float angle = (float) (random.nextDouble() * Math.PI * 2);
        PVector direction = PVector.fromAngle(angle);
        applyForce(direction);
    }
}
