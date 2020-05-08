package com.mygdx.mariobros.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mariobros.MarioBrosGame;


public class HUD implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;

    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;

    public HUD(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(MarioBrosGame.V_WIDTH, MarioBrosGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        setCountdownLabel(new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        setScoreLabel(new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        setTimeLabel(new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        setLevelLabel(new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        setWorldLabel(new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        setMarioLabel(new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE)));

        table.add(getMarioLabel()).expandX().padTop(10);
        table.add(getWorldLabel()).expandX().padTop(10);
        table.add(getTimeLabel()).expandX().padTop(10);
        table.row();
        table.add(getScoreLabel()).expandX();
        table.add(getLevelLabel()).expandX();
        table.add(getCountdownLabel()).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            worldTimer--;
            getCountdownLabel().setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Label getCountdownLabel() {
        return countdownLabel;
    }

    public void setCountdownLabel(Label countdownLabel) {
        this.countdownLabel = countdownLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    public Label getLevelLabel() {
        return levelLabel;
    }

    public void setLevelLabel(Label levelLabel) {
        this.levelLabel = levelLabel;
    }

    public Label getWorldLabel() {
        return worldLabel;
    }

    public void setWorldLabel(Label worldLabel) {
        this.worldLabel = worldLabel;
    }

    public Label getMarioLabel() {
        return marioLabel;
    }

    public void setMarioLabel(Label marioLabel) {
        this.marioLabel = marioLabel;
    }
}
