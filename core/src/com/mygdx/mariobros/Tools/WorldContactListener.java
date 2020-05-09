package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Sprites.Enemies.Enemy;
import com.mygdx.mariobros.Sprites.Items.Item;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // Identify the two fixtures
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Handle Collisions
        switch (cDef) {
            case MarioBrosGame.MARIO_HEAD_BIT | MarioBrosGame.BRICK_BIT:
            case MarioBrosGame.MARIO_HEAD_BIT | MarioBrosGame.COIN_BIT:
                if (fixA.getFilterData().categoryBits == MarioBrosGame.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
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
            case MarioBrosGame.ITEM_BIT | MarioBrosGame.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBrosGame.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBrosGame.ITEM_BIT | MarioBrosGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBrosGame.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
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
