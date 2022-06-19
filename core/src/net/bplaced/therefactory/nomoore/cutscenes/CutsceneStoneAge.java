/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of nO mooRe.
 *
 * nO mooRe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nO mooRe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nO mooRe.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bplaced.therefactory.nomoore.cutscenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.nomoore.MyGdxGame;
import net.bplaced.therefactory.nomoore.utils.MediaManager;
import net.bplaced.therefactory.nomoore.utils.Particles;
import net.bplaced.therefactory.nomoore.utils.Utils;

public class CutsceneStoneAge extends ScreenAdapter implements InputProcessor {

    private final SpriteBatch batch;
    private final ShapeRenderer sr;
    private final Sprite bg;
    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private final Sprite girl;
    private String message = "";
    private int numTouches = -1;
    private final MyGdxGame myGdxGame;
    private final Sprite stone;
    private final Sprite hominideSitzend;
    private int flipX = -1;
    private final TextureRegion hominideDuckend;
    private final Sprite hominideStehend;
    private final Particles particles;
    private final Sound tusch;
    private final Music musicStoneAge;
    private boolean paused = true;
    private boolean musicStoneAgeIsPlaying;

    public CutsceneStoneAge(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        sr = myGdxGame.sr;
        viewport = myGdxGame.viewport;
        camera = myGdxGame.camera;
        font = myGdxGame.font;

        particles = new Particles();

        bg = new Sprite(new Texture("sprites/orange.png"));
        girl = new Sprite(new Texture("sprites/girl.png"));
        stone = new Sprite(new Texture("sprites/stein_angemalt.png"));
        hominideSitzend = new Sprite(new Texture("sprites/hominide_sitzend.png"));
        hominideDuckend = new Sprite(new Texture("sprites/hominide_duckend.png"));
        hominideStehend = new Sprite(new Texture("sprites/hominide_stehend.png"));

        tusch = MediaManager.getSound("music/tusch.mp3");
        musicStoneAge = MediaManager.getMusic("music/5_steinzeit.mp3");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(Color.BLACK);
        sr.rect(-70, -70, viewport.getWorldWidth(), 70);
        sr.rect(-70, 220, viewport.getWorldWidth(), 70);
        sr.rect(-70, 0, 70, 220);
        sr.rect(500, 0, 70, 220);
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (numTouches == -1) {
            message = "";
            String msg = "1 million years ago.";
            font.draw(batch, msg, 250 - Utils.getFontWidth(msg, font) / 2, 115);
        }

        if (numTouches >= 0 && numTouches <= 10) {
            bg.draw(batch);
            particles.renderSmallFire(batch, delta, 128, 43);
            if (numTouches < 8) {
                batch.draw(stone.getTexture(), 350, 30);
            }
        }

        if (numTouches >= 0 && numTouches < 7) {
            batch.draw(girl.getTexture(), 335, 30);
        }

        if (numTouches >= 0 && numTouches < 2) {
            batch.draw(hominideSitzend.getTexture(), 40, 40);
            hominideSitzend.setPosition(160, 20);
            hominideSitzend.setScale(flipX, 1);
            hominideSitzend.draw(batch);
        }
        if (numTouches >= 2 && numTouches < 5) {
            batch.draw(hominideStehend.getTexture(), 50, 45);
            batch.draw(hominideStehend.getTexture(), 170, 25);
        }
        if (numTouches == 5) {
            batch.draw(hominideDuckend.getTexture(), 45, 45);
            batch.draw(hominideDuckend.getTexture(), 165, 25);
        }
        if (numTouches == 6) {
            batch.draw(hominideDuckend.getTexture(), 185, 45);
            batch.draw(hominideDuckend.getTexture(), 290, 25);
        }
        if (numTouches == 7) {
            batch.draw(girl.getTexture(), 315, 45);
            batch.draw(hominideDuckend.getTexture(), 260, 45);
            batch.draw(hominideDuckend.getTexture(), 270, 40);
            batch.draw(hominideDuckend.getTexture(), 100, -10);
            batch.draw(hominideDuckend.getTexture(), 35, -10);
        }
        if (numTouches == 8) {
            batch.draw(hominideDuckend.getTexture(), 320, 45);
            batch.draw(hominideDuckend.getTexture(), 330, 40);
            batch.draw(hominideDuckend.getTexture(), 220, 45);
            batch.draw(hominideDuckend.getTexture(), 260, 40);
        }
        if (numTouches == 9) {
            batch.draw(hominideDuckend.getTexture(), 320, 45);
            batch.draw(hominideDuckend.getTexture(), 330, 40);
        }
        if (numTouches == 11) {
            message = "";
            String msg = "I have been used.";
            font.draw(batch, msg, 250 - Utils.getFontWidth(msg, font) / 2, 115);
        }

        if (numTouches >= 8 && numTouches < 11) {
            batch.draw(stone.getTexture(), 350, 30);
        }

        font.draw(batch, message, 265 - Utils.getFontWidth(message, font) / 2, -29);
        batch.end();
    }

    private void update() {
        if (musicStoneAgeIsPlaying && paused) {
            numTouches++;
            paused = false;
        }
        if (paused)
            return;

        if (musicStoneAge.getPosition() >= 3.278 && numTouches < 1
                || musicStoneAge.getPosition() >= 6.429 && numTouches < 2
                || musicStoneAge.getPosition() >= 9.627 && numTouches < 3
                || musicStoneAge.getPosition() >= 12.85 && numTouches < 4
                || musicStoneAge.getPosition() >= 14.5 && numTouches < 5
                || musicStoneAge.getPosition() >= 16.08 && numTouches < 6
                || musicStoneAge.getPosition() >= 17.643 && numTouches < 7
                || musicStoneAge.getPosition() >= 19.21 && numTouches < 8
                || musicStoneAge.getPosition() >= 20.87 && numTouches < 9
                || musicStoneAge.getPosition() >= 22.44 && numTouches < 10
                || musicStoneAge.getPosition() >= 25.4 && numTouches < 11) {
            numTouches++;
        }

        if (numTouches == 0) {
            message = "Girl, help with fire!";
        }
        if (numTouches == 1) {
            message = "";
            flipX = 1;
        }
        if (numTouches == 1) {
            message = "?";
        }
        if (numTouches == 2) {
            message = "What have done to our stone?";
        }
        if (numTouches == 3) {
            message = "WHAT?";
        }
        if (numTouches == 3) {
            message = "I created these!! Me! Alone! :)";
        }
        if (numTouches == 4) {
            message = "..";
        }
        if (numTouches == 5) {
            message = "";
        }
        if (numTouches >= 11 && !musicStoneAge.isPlaying()) {
            myGdxGame.setHasShownStoneAgeCutscene(true);
            myGdxGame.showGameScreen();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!musicStoneAgeIsPlaying) {
            musicStoneAge.play();
            musicStoneAgeIsPlaying = true;
        }
        return false;
    }

    @Override
    public void show() {
        super.show();
        tusch.play();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled (float v, float v1) {
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
