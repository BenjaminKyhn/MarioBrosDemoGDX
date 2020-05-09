package com.mygdx.mariobros.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mariobros.MarioBrosGame;
import com.mygdx.mariobros.Scenes.HUD;
import com.mygdx.mariobros.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MarioBrosGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        setCategoryFilter(MarioBrosGame.DESTROYED_BIT);
        getCell().setTile(null);
        HUD.addScore(200);
        MarioBrosGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
