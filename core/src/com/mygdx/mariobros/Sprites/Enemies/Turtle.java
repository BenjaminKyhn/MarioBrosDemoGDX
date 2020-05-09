package com.mygdx.mariobros.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Turtle extends Enemy {
    public enum State {WALKING, SHELL}

    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
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
                MarioBrosGame.MARIO_BIT;

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

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && !region.isFlipX()){
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX()){
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / MarioBrosGame.PPM);
        b2body.setLinearVelocity(velocity);
    }

    @Override
    public void hitOnHead() {
        if (currentState != State.SHELL) {
            currentState = State.SHELL;
            velocity.x = 0;
        }
    }
}
