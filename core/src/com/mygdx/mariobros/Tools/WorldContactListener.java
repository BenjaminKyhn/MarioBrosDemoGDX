package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mariobros.Sprites.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // Identify the two fixtures
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Call onHeadHit() method if one of the fixtures is a head and the other extends InteractiveTileObject
        if ((fixA.getUserData() != null && fixA.getUserData().equals("head")) || (fixB.getUserData() != null && fixB.getUserData().equals("head"))){
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())){
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
