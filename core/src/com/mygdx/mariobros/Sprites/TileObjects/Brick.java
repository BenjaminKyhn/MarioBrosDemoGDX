package com.mygdx.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Scenes.HUD;
import com.mygdx.mariobros.Screens.PlayScreen;
import com.mygdx.mariobros.Sprites.Mario;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBrosGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()){
            setCategoryFilter(MarioBrosGame.DESTROYED_BIT);
            getCell().setTile(null);
            HUD.addScore(200);
            MarioBrosGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioBrosGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
