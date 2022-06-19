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

public class CutsceneMedieval extends ScreenAdapter implements InputProcessor {

	private final SpriteBatch batch;
	private final ShapeRenderer sr;
	private final Sprite bg;
	private final FitViewport viewport;
	private final OrthographicCamera camera;
	private final BitmapFont font;
	private final Sprite girl;
	private String message = "";
	private int numTouches = -1;
	private final Sprite king;
	private final MyGdxGame myGdxGame;
	private final TextureRegion mother;
	private final Particles particles;
	private final Sprite girldBurned1;
	private final Sprite girldBurned2;
	private final Sprite girldBurned3;
	private final Sprite girldBurned4;
	private final Sound tusch;
	private final Music mittelalter;
	private boolean paused = true;
	private final Music iHaveBurned;
	private final Sound slap;
	private boolean slapPlayed;
	private boolean mittelalterPlayed;

	public CutsceneMedieval(MyGdxGame myGdxGame) {
		this.myGdxGame = myGdxGame;
		batch = myGdxGame.batch;
		sr = myGdxGame.sr;
		viewport = myGdxGame.viewport;
		camera = myGdxGame.camera;
		font = myGdxGame.font;

		particles = new Particles();

		bg = new Sprite(new Texture("sprites/red.png"));
		girl = new Sprite(new Texture("sprites/girl.png"));
		king = new Sprite(new Texture("sprites/king.png"));
		mother = new Sprite(new Texture("sprites/mother.png"));
		girldBurned1 = new Sprite(new Texture("sprites/girl_burned1.png"));
		girldBurned2 = new Sprite(new Texture("sprites/girl_burned2.png"));
		girldBurned3 = new Sprite(new Texture("sprites/girl_burned3.png"));
		girldBurned4 = new Sprite(new Texture("sprites/girl_burned4.png"));

		slap = MediaManager.getSound("music/slap.mp3");
		tusch = MediaManager.getSound("music/tusch.mp3");
		mittelalter = MediaManager.getMusic("music/4_mittelalter.mp3");
		iHaveBurned = MediaManager.getMusic("music/iHaveBurned.mp3");
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
			String msg = "535 years ago.";
			font.draw(batch, msg, 250 - Utils.getFontWidth(msg, font) / 2, 115);
		}

		if (numTouches >= 0 && numTouches < 9) {
			bg.draw(batch);
			batch.draw(king.getTexture(), 200, 20);
			particles.renderBigFire(batch, delta, 150, 55);
		}

		if (numTouches > -1 && numTouches < 3) {
			batch.draw(mother.getTexture(), 60, 20);
		}
		if (numTouches >= 3 && numTouches < 9) {
			batch.draw(mother.getTexture(), 68, 20);
		}

		if (numTouches >= 0 && numTouches < 3) {
			batch.draw(girl.getTexture(), 95, 25);
			batch.draw(mother.getTexture(), 60, 20);
		}
		if (numTouches == 3) {
			batch.draw(girl.getTexture(), 130, 44);
			message = "* slap *";
		}
		if (numTouches == 4) {
			batch.draw(girldBurned1.getTexture(), 130, 44);
			message = "";
		}
		if (numTouches == 5) {
			batch.draw(girldBurned2.getTexture(), 130, 44);
		}
		if (numTouches == 6) {
			batch.draw(girldBurned3.getTexture(), 130, 44);
		}
		if (numTouches == 7) {
			batch.draw(girldBurned4.getTexture(), 130, 44);
		} else if (numTouches == 9) {
			message = "";
			String msg = "I have been burned.";
			font.draw(batch, msg, 250 - Utils.getFontWidth(msg, font) / 2, 115);
		}

		font.draw(batch, message, 265 - Utils.getFontWidth(message, font) / 2, -29);
		batch.end();
	}

	private void update() {
		if (numTouches >= 9 && iHaveBurned.getPosition() > 6.1f) {
			myGdxGame.showGameScreen();
		}

		if (mittelalter.isPlaying() && paused) {
			numTouches++;
			paused = false;
		}
		if (paused)
			return;

		// System.out.println(numFrames);

		if (mittelalter.getPosition() >= 2.8 && numTouches < 1 || mittelalter.getPosition() >= 5.492 && numTouches < 2
				|| mittelalter.getPosition() >= 8.269 && numTouches < 3
				|| mittelalter.getPosition() >= 11.046 && numTouches < 4
				|| mittelalter.getPosition() >= 13.823 && numTouches < 5
				|| mittelalter.getPosition() >= 16.507 && numTouches < 6
				|| mittelalter.getPosition() >= 19.253 && numTouches < 7
				|| iHaveBurned.getPosition() >= 0.082 && numTouches < 8
				|| iHaveBurned.getPosition() >= 1.967 && numTouches < 9) {
			numTouches++;
		}

		if (!slapPlayed && mittelalter.getPosition() >= 8.172) {
			slap.play();
			slapPlayed = true;
		}

		if (mittelalter.getPosition() >= 23.05 && !iHaveBurned.isPlaying()) {
			iHaveBurned.play();
		}

		if (numTouches == 0) {
			message = "Girl, you must give up that stupid paintin'!";
		}
		if (numTouches == 1) {
			message = "But.. but.. drawing is my passion.";
		} else if (numTouches == 2) {
			message = "Into the fire with her!";
		} else if (numTouches == 8) {
			message = "Finally she is gone.";
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
		if (!mittelalterPlayed && !mittelalter.isPlaying()) {
			mittelalter.play();
			mittelalterPlayed = true;
		}
		return false;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
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
