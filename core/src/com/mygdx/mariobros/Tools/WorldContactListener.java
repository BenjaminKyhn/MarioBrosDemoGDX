package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Sprites.Enemy;
import com.mygdx.mariobros.Sprites.InteractiveTileObject;
import com.mygdx.mariobros.Sprites.Mario;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // Identify the two fixtures
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Call onHeadHit() method if one of the fixtures is a head and the other extends InteractiveTileObject
        if ((fixA.getUserData() != null && fixA.getUserData().equals("head")) || (fixB.getUserData() != null && fixB.getUserData().equals("head"))) {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        switch (cDef) {
            case MarioBrosGame.ENEMY_HEAD_BIT | MarioBrosGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBrosGame.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead();
                else
                    ((Enemy) fixB.getUserData()).hitOnHead();
                break;
            case MarioBrosGame.ENEMY_BIT | MarioBrosGame.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBrosGame.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBrosGame.MARIO_BIT | MarioBrosGame.ENEMY_BIT:
                Gdx.app.log("MARIO", "DIED");
                break;
            case MarioBrosGame.ENEMY_BIT | MarioBrosGame.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
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
