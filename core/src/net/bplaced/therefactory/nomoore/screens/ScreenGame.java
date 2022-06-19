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

package net.bplaced.therefactory.nomoore.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.nomoore.MyGdxGame;
import net.bplaced.therefactory.nomoore.constants.Configuration;
import net.bplaced.therefactory.nomoore.entities.Interactable;
import net.bplaced.therefactory.nomoore.utils.MediaManager;
import net.bplaced.therefactory.nomoore.utils.Particles;
import net.bplaced.therefactory.nomoore.utils.Utils;

public class ScreenGame extends ScreenAdapter implements GestureListener {

    private final SpriteBatch batch;
    private final ShapeRenderer sr;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final Particles particles;
    private final MyGdxGame myGdxGame;
    private final BitmapFont font;
    private String message = "Seeing this picture I want to go to the past of this room..";
    private GestureDetector gestureDetector;

    private final Texture textureMedieval;

    private final TextureRegion regionModernTimes;
    private final TextureRegion regionStoneage;
    private final TextureRegion regionMedieval;

    private final Rectangle rectangleLeftHandle;
    private final Rectangle rectangleRightHandle;

    private final Vector2 touchCoordinates;
    private Vector2 unprojectedCoordinates;
    private final Vector2 previousPositionOfLeftHandle;
    private final Vector2 previousPositionOfRightHandle;

    private final Array<Interactable> interactablesModernTimes;
    private final Array<Interactable> interactablesMedieval;
    private final Array<Interactable> interactablesStoneAge;
    private final Array<Interactable> inventory;

    private Interactable selectedItemInInventory;
    private final Interactable canvas;
    private final Interactable easel;
    private final Interactable daddy;
    private final Interactable fan;
    private final Interactable fridge;
    private final Interactable firewood;
    private final Interactable straw;
    private final Interactable rock;
    private final Interactable flintstones;
    private final Interactable goblet;
    private final Interactable gobletBloody;
    private final Interactable fridgeOpen;
    private final Interactable milk;
    private final Interactable scalpel;
    private final Interactable charcoal;
    private final Interactable rockPainted;
    private final Interactable fireplaceUsed;
    private final Interactable picBlack;
    private final Interactable picWhite;
    private final Interactable moore;
    private final Interactable picRed;
    private final Interactable fireplace;

    private boolean iHavePutTheWood;
    private boolean iHavePutTheStraw;
    private boolean iHaveBurned;
    private boolean iHavePutTheCanvas;
    private boolean showMooresDrawing;
    private boolean showMedievalCutscene;
    private boolean showStoneAgeCutscene;
    private boolean fireIsGlowingWeakly;
    private boolean renderBigFire;
    private boolean renderSmallFire;
    private boolean hintFoundToExtendDrawingArea;
    private boolean hintFoundWhatToDraw;
    private boolean searchingForWhatToDraw;
    private boolean paintedWhite;
    private boolean paintedBlack;
    private boolean paintedRed;
    private boolean gameOver;
    private boolean bluenessRising;
    private boolean dragsRightHandle = true;
    private boolean dragsLeftHandle;
    private boolean scaleFactorRising;
    private boolean isFlinging;
    private boolean hasShownStoneAgeCutScene;

    private final Sprite spriteStar;
    private final Sprite human1;
    private final Sprite handle;
    private final Sprite pic;

    private float blueness;
    private float scaleFactor = 1;
    private float velX;
    private float panSpeedMultiplicator = 1;

    private final float[] starPositionsX;
    private final float[] starPositionsY;
    private final float[] starSpeeds;

    private final int numStars = Configuration.NUM_STARS;
    private int numTouches;

    private final Sound soundSecretFound;
    private final Sound soundCut;
    private final Sound soundFireIgnited;
    private final Sound soundItemPickup;
    private final Sound soundIHavePaintedTheStone;

    private final Music musicCreepy;
    private final Music musicMainTheme;

    public ScreenGame(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        sr = myGdxGame.sr;
        viewport = myGdxGame.viewport;
        camera = myGdxGame.camera;
        font = myGdxGame.font;

        gestureDetector = new GestureDetector(20, .5f, 2, .15f, this);

        musicMainTheme = MediaManager.getMusic("music/3_mainTheme.mp3");
        musicMainTheme.setLooping(true);

        musicCreepy = MediaManager.getMusic("music/creepyDrums.mp3");
        musicCreepy.setLooping(true);

        soundSecretFound = MediaManager.getSound("music/secretFound.mp3");
        soundIHavePaintedTheStone = MediaManager.getSound("music/iHavePaintedTheStone_sound.mp3");
        soundCut = MediaManager.getSound("music/cut.mp3");
        soundFireIgnited = MediaManager.getSound("music/fireIgnited.mp3");
        soundItemPickup = MediaManager.getSound("music/item_pickup.mp3");

        touchCoordinates = new Vector2();
        unprojectedCoordinates = new Vector2();
        previousPositionOfRightHandle = new Vector2();
        previousPositionOfLeftHandle = new Vector2();

        rectangleRightHandle = new Rectangle();
        rectangleRightHandle.setSize(32, 220);
        rectangleLeftHandle = new Rectangle();
        rectangleLeftHandle.setSize(rectangleRightHandle.width, rectangleRightHandle.height);

        handle = new Sprite(new Texture("sprites/handle.png"));

        // backgrounds
        Texture textureModernTimes = new Texture("sprites/blue.png");
        regionModernTimes = new TextureRegion(textureModernTimes);
        textureMedieval = new Texture("sprites/red.png");
        regionMedieval = new TextureRegion(textureMedieval);
        Texture textureStoneAge = new Texture("sprites/orange.png");
        regionStoneage = new TextureRegion(textureStoneAge);

        rectangleRightHandle.setY((textureModernTimes.getHeight() / 2 - rectangleRightHandle.height / 2));
        rectangleLeftHandle.setY(rectangleRightHandle.getY());

        pic = new Sprite(new Texture("sprites/pic.png"));
        spriteStar = new Sprite(new Texture("sprites/star.png"));
        human1 = new Sprite(new Texture("sprites/human1.png"));

        interactablesModernTimes = new Array<Interactable>();
        interactablesMedieval = new Array<Interactable>();
        interactablesStoneAge = new Array<Interactable>();
        inventory = new Array<Interactable>();

        // -- modern times

        fridge = new Interactable("");
        fridge.setSprite(new Sprite(new Texture("sprites/fridge.png")));
        fridge.setPosition(130, 45);
        fridge.setConsumable(false);
        interactablesModernTimes.add(fridge);

        fridgeOpen = new Interactable("");
        fridgeOpen.setSprite(new Sprite(new Texture("sprites/fridge_open.png")));
        fridgeOpen.setPosition(130, 32);
        fridgeOpen.setConsumable(false);
        fridgeOpen.setVisible(false);
        interactablesModernTimes.add(fridgeOpen);

        fan = new Interactable("fan");
        fan.setSprite(new Sprite(new Texture("sprites/faecher.png")));
        fan.setPosition(70, 100);
        interactablesModernTimes.add(fan);

        daddy = new Interactable("");
        daddy.setSprite(new Sprite(new Texture("sprites/human.png")));
        daddy.setPosition(250, 25);
        daddy.setConsumable(false);
        interactablesModernTimes.add(daddy);

        Interactable easelWithCanvas = new Interactable("");
        easelWithCanvas.setSprite(new Sprite(new Texture("sprites/staffelei.png")));
        easelWithCanvas.setPosition(340, 30);
        easelWithCanvas.setConsumable(false);
        interactablesModernTimes.add(easelWithCanvas);

        easel = new Interactable("");
        easel.setSprite(new Sprite(new Texture("sprites/staffelei.png")));
        easel.setPosition(400, 30);
        easel.setConsumable(false);
        interactablesModernTimes.add(easel);

        canvas = new Interactable("canvas");
        canvas.setSprite(new Sprite(new Texture("sprites/papier.png")));
        canvas.setPosition(343, 49);
        canvas.setConsumable(false);
        interactablesModernTimes.add(canvas);

        milk = new Interactable("carton of milk");
        milk.setSprite(new Sprite(new Texture("sprites/milk.png")));
        milk.setPosition(130, 50);
        milk.setConsumable(false);
        milk.setVisible(false);
        interactablesModernTimes.add(milk);

        scalpel = new Interactable("scalpel");
        scalpel.setSprite(new Sprite(new Texture("sprites/scalpel.png")));
        scalpel.setPosition(220, 10);
        scalpel.setConsumable(false);
        interactablesModernTimes.add(scalpel);

        Interactable paper = new Interactable("");
        paper.setSprite(new Sprite(new Texture("sprites/papier.png")));
        paper.setPosition(343, 50);
        paper.setConsumable(false);
        interactablesModernTimes.add(paper);

        moore = new Interactable("");
        moore.setSprite(new Sprite(new Texture("sprites/girl.png")));
        moore.setPosition(412, 53);
        moore.setVisible(false);
        moore.setConsumable(true);
        interactablesModernTimes.add(moore);

        // -- medieval

        goblet = new Interactable("goblet");
        goblet.setSprite(new Sprite(new Texture("sprites/kelch.png")));
        goblet.setPosition(120, 102);
        goblet.setConsumable(false);
        interactablesMedieval.add(goblet);

        gobletBloody = new Interactable("goblet filled with blood");
        gobletBloody.setSprite(new Sprite(new Texture("sprites/kelchMitBlut.png")));
        gobletBloody.setVisible(false);
        gobletBloody.setPosition(130, 107);
        interactablesModernTimes.add(gobletBloody);

        fireplace = new Interactable("");
        fireplace.setSprite(new Sprite(new Texture("sprites/feuerstelle.png")));
        fireplace.setPosition(117, 40);
        fireplace.setConsumable(false);
        interactablesMedieval.add(fireplace);

        fireplaceUsed = new Interactable("");
        fireplaceUsed.setSprite(new Sprite(new Texture("sprites/fireplace_used.png")));
        fireplaceUsed.setPosition(110, 27);
        fireplaceUsed.setVisible(false);
        fireplaceUsed.setConsumable(false);
        interactablesMedieval.add(fireplaceUsed);

        firewood = new Interactable("pile of firewood");
        firewood.setSprite(new Sprite(new Texture("sprites/holz.png")));
        firewood.setPosition(30, 15);
        interactablesMedieval.add(firewood);

        charcoal = new Interactable("pile of charcoal");
        charcoal.setSprite(new Sprite(new Texture("sprites/holzkohle.png")));
        charcoal.setPosition(132, 35);
        charcoal.setVisible(false);
        charcoal.setConsumable(false);
        interactablesMedieval.add(charcoal);

        // -- stone age

        straw = new Interactable("pile of straw");
        straw.setSprite(new Sprite(new Texture("sprites/stroh.png")));
        straw.setPosition(50, 15);
        interactablesStoneAge.add(straw);

        rockPainted = new Interactable("");
        rockPainted.setSprite(new Sprite(new Texture("sprites/stein_angemalt.png")));
        rockPainted.setPosition(290, 20);
        rockPainted.setConsumable(false);
        interactablesStoneAge.add(rockPainted);

        rock = new Interactable("");
        rock.setSprite(new Sprite(new Texture("sprites/stein.png")));
        rock.setPosition(380, 30);
        rock.setConsumable(false);
        rock.setVisible(false);
        interactablesStoneAge.add(rock);

        flintstones = new Interactable("pair of flintstones");
        flintstones.setSprite(new Sprite(new Texture("sprites/feuersteine.png")));
        flintstones.setPosition(115, 35);
        interactablesStoneAge.add(flintstones);

        picBlack = new Interactable("");
        picBlack.setSprite(new Sprite(new Texture("sprites/pic_black.png")));
        picBlack.setVisible(false);
        picBlack.setPosition(400, 40);
        picBlack.setConsumable(false);
        interactablesStoneAge.add(picBlack);

        picWhite = new Interactable("");
        picWhite.setSprite(new Sprite(new Texture("sprites/pic_white.png")));
        picWhite.setPosition(415, 85);
        picWhite.setConsumable(false);
        picWhite.setVisible(false);
        interactablesStoneAge.add(picWhite);

        picRed = new Interactable("");
        picRed.setSprite(new Sprite(new Texture("sprites/pic_red.png")));
        picRed.setConsumable(false);
        picRed.setPosition(400, 45);
        picRed.setVisible(false);
        interactablesStoneAge.add(picRed);

        // init stars
        starPositionsX = new float[numStars];
        starPositionsY = new float[numStars];
        starSpeeds = new float[numStars];
        for (int i = 0; i < numStars; i++) {
            starPositionsX[i] = Utils.randomWithin(215, 250);
            starPositionsY[i] = 150;
            starSpeeds[i] = Utils.randomWithin(100, 400);
        }

        particles = new Particles();
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        // draw outer black borders
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setAutoShapeType(true);

        // draw blue background
        sr.setColor(0, 0, blueness, 1);
        sr.rect(0, 0, Configuration.WINDOW_WIDTH, Configuration.WINDOW_HEIGHT);

        // draw outer black borders
        sr.setColor(Color.BLACK);
        sr.rect(-70, -70, viewport.getWorldWidth(), 70);
        sr.rect(-70, 220, viewport.getWorldWidth(), 70);
        sr.rect(-70, 0, 70, 220);
        sr.rect(500, 0, 70, 220);

        // highlight selected item
        if (selectedItemInInventory != null) {
            sr.setColor(new Color(.25f, .25f, .25f, 1));
            sr.circle(selectedItemInInventory.getSprite().getX() + selectedItemInInventory.getSprite().getWidth() / 2, -35, 30);
        }
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // draw stars
        for (int i = 0; i < numStars; i++) {
            batch.draw(spriteStar.getTexture(), starPositionsX[i], starPositionsY[i]);
        }

        // draw backgrounds
        batch.draw(regionModernTimes, (int) rectangleRightHandle.x, 0);
        batch.draw(regionMedieval, 0, 0);
        if (isBetweenStoneAgeAndMedieval()) {
            batch.draw(regionStoneage, 0, 0);
        }

        // draw message
        if (message != null && message.length() > 0) {
            font.setColor(Color.WHITE);
            font.draw(batch, message, textureMedieval.getWidth() / 2 - Utils.getFontWidth(message, font) / 2, 260);
        }

        // draw interactables
        if (isBetweenMedievalAndModernTimes()) {
            for (Interactable interactable : interactablesModernTimes) {
                if (interactable.isVisible() && !interactable.isConsumed()) {
                    drawSprite(batch, interactable.getSprite(), 2, interactable);
                }
            }
        }
        for (Interactable interactable : interactablesMedieval) {
            if (interactable.isVisible() && !interactable.isConsumed()) {
                drawSprite(batch, interactable.getSprite(), 1, interactable);
            }
        }
        if (isBetweenStoneAgeAndMedieval() && !dragsRightHandle) {
            for (Interactable interactable : interactablesStoneAge) {
                if (interactable.isVisible() && !interactable.isConsumed()) {
                    drawSprite(batch, interactable.getSprite(), 0, interactable);
                }
            }
        }

        // handles
        if (!gameOver && isBetweenMedievalAndModernTimes()) {
            // hide right handle if between stone age and medieval
            batch.draw(handle.getTexture(), (int) rectangleRightHandle.x, rectangleRightHandle.y, rectangleRightHandle.width, rectangleRightHandle.height);
        }
        if (!gameOver && isBetweenStoneAgeAndMedieval()) {
            // hide left handle if between medieval and modern times
            batch.draw(handle.getTexture(), (int) rectangleLeftHandle.x, rectangleLeftHandle.y, rectangleLeftHandle.width, rectangleLeftHandle.height);
        }

        // fire effects
        if (renderBigFire) {
            if (rectangleRightHandle.getX() > fireplace.getSprite().getX() + 30 && rectangleLeftHandle.getX() < fireplace.getSprite().getX() + 30) {
                particles.renderBigFire(batch, delta, fireplace.getSprite().getX() + 30, fireplace.getSprite().getY() + 20);
            }
        } else if (renderSmallFire) {
            if (rectangleRightHandle.getX() > fireplace.getSprite().getX() + 30 && rectangleLeftHandle.getX() < fireplace.getSprite().getX() + 30) {
                particles.renderSmallFire(batch, delta, fireplace.getSprite().getX() + 30, fireplace.getSprite().getY() + 20);
            }
        }

        // inventory
        for (Interactable interactable : inventory) {
            interactable.getSprite().draw(batch);
        }

        if (gameOver) {
            if (numTouches == 1) {
                message = "?";
            }
            if (numTouches == 2) {
                daddy.setVisible(false);
                batch.draw(human1, daddy.getSprite().getX() + 18, daddy.getSprite().getY() + 2);
                message = "Moore, is it you?";
            }
            if (numTouches == 3) {
                moore.setPosition(400, 25);
                message = "Yes, daddy!";
                batch.draw(human1, daddy.getSprite().getX() + 100, daddy.getSprite().getY());
            }
            if (numTouches == 4) {
                message = "";
                moore.setPosition(375, 25);
                batch.draw(human1, daddy.getSprite().getX() + 110, daddy.getSprite().getY());
            }
            if (numTouches == 5) {
                myGdxGame.showOutroScreen();
            }
        }

        // fade
        if (showMooresDrawing) {
            myGdxGame.fade.draw(batch, .6f);
            pic.draw(batch);
        }

        batch.end();
    }

    private void update(float delta) {
        // touch input
        if (isFlinging) {
            velX *= .85f;
            if (rectangleLeftHandle.getX() > 0) {
                rectangleLeftHandle.setX(Math.min(textureMedieval.getWidth(), Math.max(0, rectangleLeftHandle.getX() + velX * delta)));
            } else {
                rectangleRightHandle.setX(Math.min(textureMedieval.getWidth(), Math.max(0, rectangleRightHandle.getX() + velX * delta)));
            }
        }

        // backgrounds
        regionModernTimes.setRegionX((int) rectangleRightHandle.x);
        regionMedieval.setRegionWidth((int) rectangleRightHandle.x);
        regionStoneage.setRegionWidth((int) rectangleLeftHandle.x);

        // skycolor with breath effect
        if (bluenessRising) {
            if (blueness >= 1.1f) {
                bluenessRising = false;
            } else {
                blueness += Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            }
        } else {
            if (blueness <= .7f) {
                bluenessRising = true;
            } else {
                blueness -= Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            }
        }

        // scale factor for interactable items
        if (scaleFactorRising) {
            if (scaleFactor >= 1.05f) {
                scaleFactorRising = false;
                scaleFactor -= Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            } else {
                scaleFactor += Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            }
        } else {
            if (scaleFactor <= .95f) {
                scaleFactorRising = true;
                scaleFactor += Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            } else {
                scaleFactor -= Configuration.ANIMATION_SPEED_OF_BREATHING_SKYCOLOR;
            }
        }

        // stars
        if (!hasShownStoneAgeCutScene || gameOver) {
            for (int i = 0; i < numStars; i++) {
                starPositionsY[i] = starPositionsY[i] - starSpeeds[i] / 1000;
                if (starPositionsY[i] < 50) {
                    starPositionsY[i] = 150;
                }
            }
        }

        if (hasShownStoneAgeCutScene && !gameOver && paintedBlack && paintedRed && paintedWhite)
            message = "What a beautiful pain ting.";
    }

    private void drawSprite(SpriteBatch batch, Sprite sprite, int scene, Interactable interactable) {
        if (interactable.isConsumed()) {
            sprite.draw(batch);
            return;
        }
        Rectangle handle = null;
        if (dragsLeftHandle)
            handle = rectangleLeftHandle;
        if (dragsRightHandle)
            handle = rectangleRightHandle;
        if (handle == null)
            return;
        float percentage = (Math.min(sprite.getWidth(), Math.max(0, handle.x - sprite.getX())) / sprite.getWidth());
        boolean reverseDirection = (scene == 1 && dragsLeftHandle || scene == 2 && dragsRightHandle);
        if (reverseDirection) {
            percentage = 1 - percentage;
        }
        int visibleWidth = (int) (sprite.getWidth() * percentage);
        batch.draw(sprite.getTexture(), (reverseDirection ? sprite.getWidth() - visibleWidth : 0) + sprite.getX(), sprite.getY(), sprite.getWidth() / 2, sprite.getHeight() / 2, visibleWidth, sprite.getHeight(), sprite.getScaleX() * (interactable.isConsumable() ? scaleFactor : 1),
                sprite.getScaleY() * (interactable.isConsumable() ? scaleFactor : 1), 0, sprite.getRegionX(), sprite.getRegionY(), (int) (sprite.getRegionWidth() * percentage), sprite.getRegionHeight(), reverseDirection, false);
    }

    private boolean isBetweenStoneAgeAndMedieval() {
        return rectangleRightHandle.getX() >= textureMedieval.getWidth();
    }

    private boolean isBetweenMedievalAndModernTimes() {
        return rectangleRightHandle.getX() >= 0 && rectangleLeftHandle.getX() == 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void handleTouchOnInteractable(Interactable interactable, int scene) {
        if (interactable.isVisible() && interactable.getSprite().getBoundingRectangle().contains(unprojectedCoordinates)) {
            switch (scene) {
                case 0:
                    handleTouchesInStoneAge(interactable);
                    break;
                case 1:
                    handleTouchesInMedieval(interactable);
                    break;
                case 2:
                    handleTouchesInModernTimes(interactable);
                    break;
            }

            // item in the inventory touched
            if (inventory.contains(interactable, true) && interactable.isConsumed()) {
                toggleInventoryItem(interactable);
            }

        }
    }

    private void handleTouchesInModernTimes(Interactable interactable) {
        if (rectangleRightHandle.getX() < interactable.getSprite().getX()) {
            if (interactable.equals(daddy)) {
                if (selectedItemInInventory != null && selectedItemInInventory.equals(scalpel)) {
                    if (inventory.contains(goblet, true)) {
                        inventory.removeValue(selectedItemInInventory, true);
                        inventory.removeValue(scalpel, true);
                        inventory.removeValue(goblet, true);
                        gobletBloody.setVisible(true);
                        gobletBloody.setConsumed(true);
                        gobletBloody.setConsumable(false);
                        consumeInteractable(gobletBloody);
                        selectedItemInInventory = gobletBloody;
                        message = "I have cut.";
                        soundCut.play();
                        sortInventory();
                    } else {
                        message = "I want to cut, but I need something to collect the blood.";
                        goblet.setConsumable(true);
                    }
                } else if (showMooresDrawing) {
                    showMooresDrawing = false;
                } else if (!(paintedBlack && paintedRed && paintedWhite)) {
                    showMooresDrawing = true;
                    if (searchingForWhatToDraw) {
                        message = "She is no moore.. but was so red, black and white.";
                        if (!hintFoundWhatToDraw) {
                            soundSecretFound.play();
                            daddy.setConsumable(false);
                        }
                        milk.setConsumable(true);
                        scalpel.setConsumable(true);
                        charcoal.setConsumable(true);
                        hintFoundWhatToDraw = true;
                    } else {
                        message = "She is no moore..";
                    }
                }
            } else if (interactable.equals(moore)) {
                moore.setConsumable(false);
                message = "Daddy?";
                rectangleRightHandle.setX(0);
                gameOver = true;
                if (musicCreepy.isPlaying())
                    musicCreepy.stop();
                if (musicMainTheme.isPlaying())
                    musicMainTheme.pause();
            } else if (interactable.equals(easel)) {
                if (selectedItemInInventory != null && selectedItemInInventory.equals(canvas)) {
                    inventory.removeValue(selectedItemInInventory, true);
                    canvas.setPosition(easel.getSprite().getX() + 3, easel.getSprite().getY() + 20);
                    canvas.setConsumed(false);
                    canvas.setConsumable(false);
                    selectedItemInInventory = null;
                    message = "I have put the canvas. I have moore space to draw now.";
                    iHavePutTheCanvas = true;
                    rock.setVisible(true);
                    easel.setConsumable(false);
                    soundSecretFound.play();
                    sortInventory();
                } else {
                    if (iHavePutTheCanvas) {
                        message = "I have moore space to draw now.";
                    } else {
                        message = "An empty easel.";
                    }
                }
            } else if (interactable.equals(fridge)) {
                if (iHaveBurned) {
                    // the fridge opens
                    if (fridge.isVisible()) {
                        fridge.setVisible(false);
                        fridgeOpen.setVisible(true);
                        milk.setVisible(true);
                        scalpel.setVisible(true);
                        message = "My way has been lit.";
                        renderSmallFire = false;
                        renderBigFire = false;

                        // fire disappears and leaves charcoal for drawing
                        // purposes
                        charcoal.setVisible(true);
                        fireplaceUsed.setVisible(true);
                        interactablesMedieval.removeValue(firewood, true);
                        interactablesMedieval.removeValue(straw, true);
                        interactablesStoneAge.removeValue(straw, true);
                        straw.setVisible(false);
                    }
                } else {
                    message = "I want to see, but there is no light inside.";
                }
            } else {
                if (interactable.isConsumable() && !interactable.isConsumed()) {
                    if (interactable.equals(milk)) {
                        if (hintFoundWhatToDraw) { // only pick up if already know what to draw
                            interactable.setConsumed(true);
                            consumeInteractable(interactable);
                            soundItemPickup.play();
                        }
                    } else {
                        interactable.setConsumed(true);
                        consumeInteractable(interactable);
                        soundItemPickup.play();
                    }
                    if (interactable.equals(canvas))
                        easel.setConsumable(true);
                }
            }
        }
    }

    private void handleTouchesInMedieval(Interactable interactable) {
        if (rectangleLeftHandle.getX() < interactable.getSprite().getX() && interactable.getSprite().getX() < rectangleRightHandle.getX()) {
            if (interactable.equals(fireplace)) {
                if (selectedItemInInventory == null) { // nothing selected
                    handleTouchOnFirePlaceIfNothingSelectedInInventory();
                } else if (selectedItemInInventory.equals(fan)) {
                    if (fireIsGlowingWeakly) {
                        inventory.removeValue(selectedItemInInventory, true);
                        interactablesMedieval.removeValue(fan, true);
                        selectedItemInInventory = null;
                        message = "I have burned.";
                        renderBigFire = true;
                        renderSmallFire = false;
                        iHaveBurned = true;
                        showMedievalCutscene = true;
                        soundSecretFound.play();
                        sortInventory();
                    } else {
                        message = "I want to burn, but something is missing..";
                    }
                } else if (selectedItemInInventory.equals(flintstones)) {
                    if (iHavePutTheStraw && iHavePutTheWood) {
                        inventory.removeValue(selectedItemInInventory, true);
                        straw.setConsumed(false);
                        straw.setConsumable(false);
                        interactablesStoneAge.removeValue(straw, true);
                        interactablesMedieval.removeValue(straw, true);
                        selectedItemInInventory = null;
                        message = "The fire is still glowing too weakly.";
                        fireIsGlowingWeakly = true;
                        renderSmallFire = true;
                        soundFireIgnited.play();
                        sortInventory();
                    } else {
                        message = "I want to burn, but something is missing..";
                    }
                } else if (selectedItemInInventory.equals(straw)) {
                    inventory.removeValue(selectedItemInInventory, true);
                    straw.setPosition(fireplace.getSprite().getX() + 17, fireplace.getSprite().getY() + 15);
                    straw.setConsumed(false);
                    straw.setConsumable(false);
                    interactablesMedieval.add(straw);
                    interactablesStoneAge.removeValue(straw, true);
                    selectedItemInInventory = null;
                    message = "I have put the straw.";
                    iHavePutTheStraw = true;
                    sortInventory();
                } else if (selectedItemInInventory.equals(firewood)) {
                    inventory.removeValue(selectedItemInInventory, true);
                    firewood.setPosition(fireplace.getSprite().getX() + 4, fireplace.getSprite().getY() - 5);
                    firewood.setConsumed(false);
                    firewood.setConsumable(false);
                    selectedItemInInventory = null;
                    message = "I have put the firewood.";
                    iHavePutTheWood = true;
                    sortInventory();
                } else { // nothing selected
                    handleTouchOnFirePlaceIfNothingSelectedInInventory();
                }
            } else {
                if (interactable.isConsumable() && !interactable.isConsumed()) {
                    if (interactable.equals(charcoal)) {
                        if (hintFoundWhatToDraw) { // only pick up if already know what to draw
                            interactable.setConsumed(true);
                            consumeInteractable(interactable);
                            soundItemPickup.play();
                            renderSmallFire = false;
                        }
                    } else {
                        interactable.setConsumed(true);
                        consumeInteractable(interactable);
                        soundItemPickup.play();
                    }
                }
            }
        }
    }

    private void handleTouchOnFirePlaceIfNothingSelectedInInventory() {
        if (iHaveBurned) {
            message = "I have burned.";
        } else if (fireIsGlowingWeakly) {
            message = "The fire is still glowing too weakly.";
        } else {
            message = "I want to burn, but something is missing..";
        }
    }

    private void handleTouchesInStoneAge(Interactable interactable) {
        if (rectangleLeftHandle.getX() > interactable.getSprite().getX()) {
            if (interactable.equals(rockPainted)) {
                message = "I want to draw, but there is no moore space.";
                if (!hintFoundToExtendDrawingArea)
                    canvas.setConsumable(true);
                hintFoundToExtendDrawingArea = true;
            } else if (interactable.equals(rock)) {
                if (hintFoundWhatToDraw) {
                    if (selectedItemInInventory == null) {
                        if (moore.isVisible()) {
                            message = "What a beautiful pain ting.";
                        } else if (paintedBlack && paintedRed && paintedWhite) {
                            soundSecretFound.play();
                            message = "What a beautiful pain ting.";
                            moore.setVisible(true);
                            showStoneAgeCutscene = true;
                            canvas.setConsumable(false);
                        } else {
                            message = "I want to draw, but something is missing..";
                        }
                    } else if (selectedItemInInventory.equals(gobletBloody)) {
                        inventory.removeValue(gobletBloody, true);
                        picRed.setVisible(true);
                        selectedItemInInventory = null;
                        message = "No moore blood to shed.";
                        paintedRed = true;
                        soundIHavePaintedTheStone.play();
                        sortInventory();
                    } else if (selectedItemInInventory.equals(charcoal)) {
                        inventory.removeValue(charcoal, true);
                        picBlack.setVisible(true);
                        selectedItemInInventory = null;
                        message = "No moore flesh to burn.";
                        paintedBlack = true;
                        soundIHavePaintedTheStone.play();
                        sortInventory();
                    } else if (selectedItemInInventory.equals(milk)) {
                        inventory.removeValue(milk, true);
                        picWhite.setVisible(true);
                        selectedItemInInventory = null;
                        message = "No moore girl to humiliate.";
                        paintedWhite = true;
                        soundIHavePaintedTheStone.play();
                        sortInventory();
                    } else {
                        if (paintedBlack && paintedRed && paintedWhite) {
                            message = "..";
                            myGdxGame.showStoneAgeCutscene();
                        } else {
                            message = "I want to draw, but something is missing..";
                        }
                    }
                } else {
                    message = "I want to draw, but I don't know what.";
                    searchingForWhatToDraw = true;
                    daddy.setConsumable(true);
                }
            } else if (interactable.isConsumable() && !interactable.isConsumed()) {
                interactable.setConsumed(true);
                consumeInteractable(interactable);
                soundItemPickup.play();
            }
        }
    }

    private void toggleInventoryItem(Interactable interactable) {
        if (selectedItemInInventory == null) { // select
            selectedItemInInventory = interactable;
        } else {
            if (selectedItemInInventory == interactable) { // deselect
                selectedItemInInventory = null;
                message = "";
            } else {
                selectedItemInInventory = interactable; // instantly select after consume
            }
        }
        if (selectedItemInInventory != null)
            message = "This is a " + interactable.getName() + ".";
    }

    private void consumeInteractable(Interactable interactable) {
        int x = 20;
        for (int i = 0; i < inventory.size; i++) {
            x += inventory.get(i).getSprite().getWidth() + 20;
        }
        interactable.setPosition(x, -50);
        inventory.add(interactable);
    }

    private void sortInventory() {
        int x = 20;
        for (int i = 0; i < inventory.size; i++) {
            Interactable interactable = inventory.get(i);
            interactable.setPosition(x, -50);
            x += interactable.getSprite().getWidth() + 20;
        }
    }

    @Override
    public void show() {
        super.show();
        if (paintedBlack && paintedRed && paintedWhite) {
            musicCreepy.play();
//            panSpeedMultiplicator = .0001f; // Probably meant to be an effect, but I just found it frustrating and thought it was a glitch.
        } else {
            if (!musicMainTheme.isPlaying())
                musicMainTheme.play();
        }
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void hide() {
        super.hide();
        if (musicMainTheme.isPlaying())
            musicMainTheme.pause();
        if (paintedBlack && paintedRed && paintedWhite) {
            musicCreepy.pause();
        }
    }

    public void cleanUpMainThemeMusic() {
        if (musicMainTheme.isPlaying()) {
            musicMainTheme.pause();
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        isFlinging = false;
        if (showMedievalCutscene) {
            showMedievalCutscene = false;
            myGdxGame.showMedievalCutscene();
        } else if (showStoneAgeCutscene) {
            showStoneAgeCutscene = false;
            myGdxGame.showStoneAgeCutscene();
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        // Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y + ", count: " + count);
        if (gameOver) {
            numTouches++;
            return true;
        }

        touchCoordinates.set(x, y);
        unprojectedCoordinates = viewport.unproject(touchCoordinates);

        boolean touchingRightHandle = rectangleRightHandle.contains(unprojectedCoordinates) && isBetweenMedievalAndModernTimes();
        boolean touchingLeftHandle = rectangleLeftHandle.contains(unprojectedCoordinates) && isBetweenStoneAgeAndMedieval();

        if (touchingRightHandle || touchingLeftHandle) {
            dragsLeftHandle = touchingLeftHandle;
            dragsRightHandle = touchingRightHandle;
        }

        previousPositionOfRightHandle.set(rectangleRightHandle.getX(), rectangleRightHandle.getY());
        previousPositionOfLeftHandle.set(rectangleLeftHandle.getX(), rectangleLeftHandle.getY());

        // handle touches on interactables
        if (!touchingRightHandle && !touchingLeftHandle && !showMooresDrawing) {
            for (Interactable interactable : interactablesModernTimes) {
                handleTouchOnInteractable(interactable, 2);
            }
            for (Interactable interactable : interactablesMedieval) {
                handleTouchOnInteractable(interactable, 1);
            }
            for (Interactable interactable : interactablesStoneAge) {
                handleTouchOnInteractable(interactable, 0);
            }
        } else if (showMooresDrawing) {
            showMooresDrawing = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        // Gdx.app.log("GestureDetectorTest", "fling " + velocityX + ", " +
        // velocityY);
        isFlinging = true;
        float signum = Math.signum(velocityX);
        velX = Math.min(1000, Math.abs(velocityX)) * signum;
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        // Gdx.app.log("GestureDetectorTest", "pan at " + x + ", " + y);
        if (showMooresDrawing) {
            showMooresDrawing = false;
        }
        deltaX *= viewport.getWorldWidth() / (float) viewport.getScreenWidth();
        deltaX *= panSpeedMultiplicator;
        dragsLeftHandle = rectangleLeftHandle.getX() > 0 || rectangleLeftHandle.getX() == 0 && rectangleRightHandle.getX() >= textureMedieval.getWidth() && deltaX > 0;
        dragsRightHandle = !dragsLeftHandle;
        if (dragsLeftHandle) {
            rectangleLeftHandle.setX(Math.min(textureMedieval.getWidth(), Math.max(0, rectangleLeftHandle.getX() + deltaX)));
        } else {
            rectangleRightHandle.setX(Math.min(textureMedieval.getWidth(), Math.max(0, rectangleRightHandle.getX() + deltaX)));
        }
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        // Gdx.app.log("GestureDetectorTest", "pan stop at " + x + ", " + y);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void pinchStop() {
        // TODO Auto-generated method stub
    }

    public void setHastShownStoneAgeCutscene(boolean hasShownStoneAgeCutScene) {
        this.hasShownStoneAgeCutScene = hasShownStoneAgeCutScene;
    }
}
