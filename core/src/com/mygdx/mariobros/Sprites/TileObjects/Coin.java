package com.mygdx.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Scenes.HUD;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Items.ItemDef;
import com.mygdx.mariobros.Sprites.Items.Mushroom;
import com.mygdx.mariobros.Sprites.Mario;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBrosGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLANK_COIN)
            MarioBrosGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBrosGame.PPM),
                        Mushroom.class));
                MarioBrosGame.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else
                MarioBrosGame.manager.get("audio/sounds/coin.wav", Sound.class).play();
        }

        getCell().setTile(tileSet.getTile(BLANK_COIN));
        HUD.addScore(200);
    }
}
