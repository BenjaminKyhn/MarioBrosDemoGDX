package com.mygdx.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Goal {
    protected World world;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject object;

    public Goal(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape  = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBrosGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBrosGame.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / MarioBrosGame.PPM, bounds.getHeight() / 2 / MarioBrosGame.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

        Filter filter = new Filter();
        filter.categoryBits = MarioBrosGame.GOAL_BIT;
        fixture.setFilterData(filter);
    }
}
