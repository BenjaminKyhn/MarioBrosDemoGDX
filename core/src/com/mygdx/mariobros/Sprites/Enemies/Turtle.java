package com.mygdx.mariobros.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Fireball;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Tools.B2WorldCreator;

public class Turtle extends Enemy {
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}

    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private float deadRotationDegrees;
    private boolean setToDestroy;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), i * 16, 0, 16, 24));
        }

        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24); // fix later

        walkAnimation = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;
        B2WorldCreator.removeTurtle(this);

        setBounds(getX(), getY(), 16 / MarioBrosGame.PPM, 24 / MarioBrosGame.PPM);
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
        fdef.restitution = 1.5f; // adds bounce to collisions
        fdef.filter.categoryBits = MarioBrosGame.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this); // adds access to this class from the collision handler WorldContactListener
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / MarioBrosGame.PPM);

        if (currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(b2body);
                destroyed = true;
                B2WorldCreator.removeTurtle(this);
            }
        } else
            b2body.setLinearVelocity(velocity);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void onFireballHit(Fireball fireball) {
        killed();
        MarioBrosGame.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle) {
            if (((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        } else if (currentState != State.MOVING_SHELL)
            reverseVelocity(true, false);
    }

    public State getCurrentState() {
        return currentState;
    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBrosGame.NOTHING_BIT;

        for (Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);

        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
