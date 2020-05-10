package com.mygdx.mariobros.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Enemies.Enemy;
import com.mygdx.mariobros.Sprites.Enemies.Turtle;
import com.mygdx.mariobros.Sprites.TileObjects.Brick;
import com.mygdx.mariobros.Sprites.TileObjects.Coin;
import com.mygdx.mariobros.Sprites.Enemies.Goomba;
import com.mygdx.mariobros.Sprites.TileObjects.Goal;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private static Array<Turtle> turtles;
    private Goal goal;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Create ground bodies/fixtures (index 2 in the tmx layers)
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBrosGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBrosGame.PPM);
            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / MarioBrosGame.PPM, rect.getHeight() / 2 / MarioBrosGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // Create pipe bodies/fixtures (index 3 in the tmx layers)
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBrosGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBrosGame.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBrosGame.PPM, rect.getHeight() / 2 / MarioBrosGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBrosGame.OBJECT_BIT;
            body.createFixture(fdef);
        }

        // Create goal fixtures (index 8 in the tmx layers)
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            goal = new Goal(screen, object);
        }

        // Create brick bodies/fixtures (index 5 in the tmx layers)
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }

        // Create coins bodies/fixtures (index 4 in the tmx layers)
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        // Create all goombas
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / MarioBrosGame.PPM, rect.getY() / MarioBrosGame.PPM));
        }

        // Create all turtles
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / MarioBrosGame.PPM, rect.getY() / MarioBrosGame.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

    public static void removeTurtle(Turtle turtle){
        turtles.removeValue(turtle, true);
    }

    public Goal getGoal(){
        return goal;
    }
}
