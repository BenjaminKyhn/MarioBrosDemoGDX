package com.mygdx.mariobros.Sprites.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Fireball;
import com.mygdx.mariobros.Sprites.Mario;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    PlayScreen screen;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.screen = screen;
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBrosGame.PPM, 16 / MarioBrosGame.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            // Didn't work without instantiating the PlayScreen in the class
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Fixture definition
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBrosGame.PPM);
        fdef.filter.categoryBits = MarioBrosGame.ENEMY_BIT;
        fdef.filter.maskBits = MarioBrosGame.GROUND_BIT |
                MarioBrosGame.COIN_BIT |
                MarioBrosGame.BRICK_BIT |
                MarioBrosGame.ENEMY_BIT |
                MarioBrosGame.OBJECT_BIT |
                MarioBrosGame.MARIO_BIT |
                MarioBrosGame.FIREBALL_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // Create head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1 / MarioBrosGame.PPM);
        vertices[1] = new Vector2(5, 8).scl(1 / MarioBrosGame.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / MarioBrosGame.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / MarioBrosGame.PPM);
        head.set(vertices);

        fdef.shape = head;
        fdef.restitution = 0.5f; // adds bounce to collisions
        fdef.filter.categoryBits = MarioBrosGame.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this); // adds access to this class from the collision handler WorldContactListener
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead(Mario mario) {
        Gdx.app.log("GOOMBA", "HIT");
        setToDestroy = true;
        MarioBrosGame.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
            setToDestroy = true;
        else
            reverseVelocity(true, false);
    }

    public void onFireballHit(Fireball fireball) {
        Gdx.app.log("FIREBALL", "HIT");
        setToDestroy = true;
        MarioBrosGame.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }
}
