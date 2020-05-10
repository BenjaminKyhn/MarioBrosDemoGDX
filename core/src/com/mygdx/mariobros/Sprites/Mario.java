package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Enemies.Enemy;
import com.mygdx.mariobros.Sprites.Enemies.Turtle;

public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}

    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private TextureRegion fireMarioStand;
    private TextureRegion fireMarioJump;
    private Animation<TextureRegion> fireMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTimer;
    private float hitTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean fireMario;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToDefineFireMario;
    private boolean timeToRedefineBigMario;
    private boolean marioIsDead;
    private boolean marioIsHit;

    private PlayScreen screen;

    private Array<Fireball> fireballs;

    public Mario(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        hitTimer = 0;
        runningRight = true;

        // Initialize animations
        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Create texture region for Mario running
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fire_mario"), i * 16, 0, 16, 32));
        }
        fireMarioRun = new Animation(0.1f, frames);
        frames.clear();

        // Set animation frames for growing Mario
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        }
        growMario = new Animation(0.2f, frames);

        // Create texture region for Mario jumping
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        fireMarioJump = new TextureRegion(screen.getAtlas().findRegion("fire_mario"), 80, 0, 16, 32);

        // Create texture region for Mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        fireMarioStand = new TextureRegion(screen.getAtlas().findRegion("fire_mario"), 0, 0, 16, 32);

        // Create texture region for dead Mario
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        // Define Mario in Box2d
        defineMario();

        setBounds(0, 0, 16 / MarioBrosGame.PPM, 16 / MarioBrosGame.PPM);
        setRegion(marioStand);

        fireballs = new Array<Fireball>();

        marioIsHit = false;
    }

    public void update(float dt) {
        if (marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBrosGame.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineBigMario) // TODO: Fix this?
            redefineMario();

        for (Fireball fireball : fireballs) {
            fireball.update(dt);
            if (fireball.isDestroyed())
                fireballs.removeValue(fireball, true);
        }
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = fireMario ? fireMarioJump : marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = fireMario ? fireMarioRun.getKeyFrame(stateTimer, true) : marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = fireMario ? fireMarioStand : marioIsBig ? bigMarioStand : marioStand;
                break;

            //TODO: Shrink Mario when he takes damage instead of killing him off
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBrosGame.PPM, 32 / MarioBrosGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Fixture definition
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBrosGame.PPM);
        fdef.filter.categoryBits = MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = MarioBrosGame.GROUND_BIT |
                MarioBrosGame.COIN_BIT |
                MarioBrosGame.BRICK_BIT |
                MarioBrosGame.ENEMY_BIT |
                MarioBrosGame.OBJECT_BIT |
                MarioBrosGame.ENEMY_HEAD_BIT |
                MarioBrosGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // Head collision
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), new Vector2(2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM));
        fdef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioBrosGame.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Fixture definition
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBrosGame.PPM);
        fdef.filter.categoryBits = MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = MarioBrosGame.GROUND_BIT |
                MarioBrosGame.COIN_BIT |
                MarioBrosGame.BRICK_BIT |
                MarioBrosGame.ENEMY_BIT |
                MarioBrosGame.OBJECT_BIT |
                MarioBrosGame.ENEMY_HEAD_BIT |
                MarioBrosGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBrosGame.PPM));
        b2body.createFixture(fdef).setUserData(this);

        // Head collision
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), new Vector2(2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM));
        fdef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToDefineBigMario = false;
    }

    public void redefineMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Fixture definition
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBrosGame.PPM);
        fdef.filter.categoryBits = MarioBrosGame.MARIO_BIT;
        fdef.filter.maskBits = MarioBrosGame.GROUND_BIT |
                MarioBrosGame.COIN_BIT |
                MarioBrosGame.BRICK_BIT |
                MarioBrosGame.ENEMY_BIT |
                MarioBrosGame.OBJECT_BIT |
                MarioBrosGame.ENEMY_HEAD_BIT |
                MarioBrosGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // Head collision
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM), new Vector2(2 / MarioBrosGame.PPM, 6 / MarioBrosGame.PPM));
        fdef.filter.categoryBits = MarioBrosGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineBigMario = false;
    }

    public State getState() {
        if (marioIsDead)
            return State.DEAD;
        else if (runGrowAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow() {
        if (!marioIsBig) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
            MarioBrosGame.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public void powerUp() {
        if (marioIsBig) {
            fireMario = true;
            timeToDefineFireMario = true;
            MarioBrosGame.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    // TODO: Implement invulnerability frames when taking damage
    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                fireMario = false;
                timeToRedefineBigMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBrosGame.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                MarioBrosGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBrosGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBrosGame.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void fire() {
        if (fireMario)
            fireballs.add(new Fireball(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
        // TODO: Fix the fireball speed and stop them from crashing the game
    }

    public void draw(Batch batch) {
        super.draw(batch);
        for (Fireball fireball : fireballs)
            fireball.draw(batch);
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }
}
