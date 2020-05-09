package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Scenes.HUD;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBrosGame.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        if (getCell().getTile().getId() == BLANK_COIN)
            MarioBrosGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else
            MarioBrosGame.manager.get("audio/sounds/coin.wav", Sound.class).play();
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        HUD.addScore(200);
    }
}
