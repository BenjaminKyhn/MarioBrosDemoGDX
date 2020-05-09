package com.mygdx.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Scenes.HUD;
import com.mygdx.mariobros.Sprites.Enemies.Enemy;
import com.mygdx.mariobros.Sprites.Items.Item;
import com.mygdx.mariobros.Sprites.Items.ItemDef;
import com.mygdx.mariobros.Sprites.Items.Mushroom;
import com.mygdx.mariobros.Sprites.Mario;
import com.mygdx.mariobros.Tools.B2WorldCreator;
import com.mygdx.mariobros.Tools.WorldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private MarioBrosGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private HUD hud;
    private Viewport gamePort;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Sprites
    public Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    public PlayScreen(MarioBrosGame game){
        this.game = game;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBrosGame.V_WIDTH / MarioBrosGame.PPM, MarioBrosGame.V_HEIGHT / MarioBrosGame.PPM, gamecam);
        hud = new HUD(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("MarioLevel01.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBrosGame.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -10), true);

        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBrosGame.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        if (player.currentState != Mario.State.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.W))
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        // Takes one step in the physics simulation (60 times per second)
        world.step(1/60f, 6, 2);

        // Update the player
        player.update(dt);
        for (Enemy enemy: creator.getGoombas()){
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBrosGame.PPM)
                enemy.b2body.setActive(true);
        }

        for (Item item : items)
            item.update(dt);

        hud.update(dt);

        // Fix the camera to the player if he's not dead
        if (player.currentState != Mario.State.DEAD){
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Clear the screen
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the game map
        renderer.render();

        // Render Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        // Render Mario
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy: creator.getGoombas())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        // Draw texture to screen
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 1)
            return true;
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public World getWorld(){
        return world;
    }

    public TiledMap getMap(){
        return map;
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

}
