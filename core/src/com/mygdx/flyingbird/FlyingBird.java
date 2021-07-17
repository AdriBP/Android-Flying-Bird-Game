package com.mygdx.flyingbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.files.*;

import java.util.Random;

public class FlyingBird extends ApplicationAdapter {
	Sound sound;
	SpriteBatch batch;
	ShapeRenderer hitboxShape;
	int score = 0;
	int state = 0;
	int numberOfBees = 3;
	float gravity = 0.8f;
	float velocity = 0.0f;
	BitmapFont font, text;
	Texture img, bird, bee1, bee2, bee3;
	Boolean flag = true, soundFlag = true;
	Circle birdCircle, enemy1Circle[], enemy2Circle[], enemy3Circle[];
	float birdWidth, birdHeight, screenWidth, screenHeight, birdX, birdY;
	float bees[] = new float[numberOfBees]; // Automatically generated bees
	float beesY[][] = new float[3][numberOfBees]; // Automatically generate 3 bees on Y position

	@Override
	public void create () {
		batch = new SpriteBatch();
		// Initial game image
		img = new Texture("storyboard6.png");
		// Main character set
		bird = new Texture("sprites/lead_bird/frame-1.png");
		// Enemies set
		bee1 = new Texture("sprites/enemy/enemy_1.png");
		bee2 = new Texture("sprites/enemy/enemy_1.png");
		bee3 = new Texture("sprites/enemy/enemy_1.png");

		// Enemy hitbox
		birdCircle = new Circle();
		enemy1Circle = new Circle[numberOfBees];
		enemy2Circle = new Circle[numberOfBees];
		enemy3Circle = new Circle[numberOfBees];
		hitboxShape = new ShapeRenderer();

		// Font with the score
		font = new BitmapFont();
		font.setColor(Color.RED);
		font.getData().setScale(3);
		text = new BitmapFont();
		text.setColor(Color.CYAN);
		text.getData().setScale(4);

		// Background sound
		sound = Gdx.audio.newSound(Gdx.files.internal("doh.wav"));

		// Calculate the height and width in the initial creation
		birdX = Gdx.graphics.getWidth() / 4;
		birdY = Gdx.graphics.getWidth() / 4;
		birdWidth = Gdx.graphics.getHeight() / 12;
		birdHeight = Gdx.graphics.getHeight() / 11;
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		// Enemy set position
		for (int i = 0; i < numberOfBees; i++) {
			bees[i] = screenWidth + (i * screenWidth / 2);
			Random r1 = new Random();
			Random r2 = new Random();
			Random r3 = new Random();

			// Set three different bees, each on a random position on Y
			beesY[0][i] = r1.nextFloat() * screenHeight;
			beesY[1][i] = r2.nextFloat() * screenHeight;
			beesY[2][i] = r3.nextFloat() * screenHeight;

			// Set collapse circle on enemies
			enemy1Circle[i] = new Circle();
			enemy2Circle[i] = new Circle();
			enemy3Circle[i] = new Circle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		// Set the first image in x = 0, y = 0 as the screen size
		batch.draw(img, 0, 0, screenWidth, screenHeight);

		// Main character set
		batch.draw(bird, birdX, birdY, birdWidth, birdHeight);

		// Only enable this actions if the game is on (state as 1)
		if (state == 1) {
			// Set an action when the user touch the screen
			if (Gdx.input.justTouched()) {
				// Make the bird jump 20 points
				velocity = -20;
			}
			soundFlag = true;

			// Set enemy random apparition
			for (int i = 0; i < numberOfBees; i++) {
				// If the 3 enemies have left, keep generating new ones (guarrada máxima)
				if (bees[i] < 0) {
					flag = true;
					bees[i] = numberOfBees * (screenWidth / 2);
					Random r1 = new Random();
					Random r2 = new Random();
					Random r3 = new Random();

					// Set three different bees, each on a random position on Y
					beesY[0][i] = r1.nextFloat() * screenHeight;
					beesY[1][i] = r2.nextFloat() * screenHeight;
					beesY[2][i] = r3.nextFloat() * screenHeight;
				}

				// Print the score
				font.draw(batch, String.valueOf(score), screenWidth - birdWidth, birdHeight);

				if (birdX > bees[i] && flag) {
					score++;
					flag = false;
				}

				// Set automatic direction for the enemies
				bees[i] = bees[i] - 6;

				// Enemy character set
				batch.draw(bee1, bees[i], beesY[0][i], birdWidth, birdHeight);
				batch.draw(bee2, bees[i], beesY[1][i], birdWidth, birdHeight);
				batch.draw(bee3, bees[i], beesY[2][i], birdWidth, birdHeight);
			}

			if (birdY < birdHeight) {
				birdY = Gdx.graphics.getHeight() / 3;
				velocity = 0;

				// State 2 means we lost
				state = 2;
			}
			else {
				// Set gravity to the bird
				birdY = birdY - velocity;
				velocity = velocity + gravity;
			}
		}
		// If the user has lost the game, let him restart by touching the screen
		else if (state == 2) {
			text.draw(batch, "You lost! Tap to screen to restart!", screenWidth / 2, screenHeight / 2);
			// Control to make the sound play only once
			if (soundFlag) {
				sound.play();
				soundFlag = false;
			}

			// Set an action when the user touch the screen
			if (Gdx.input.justTouched()) {
				birdX = Gdx.graphics.getWidth() / 4;
				birdY = Gdx.graphics.getWidth() / 4;
				// Enemy set position
				for (int i = 0; i < numberOfBees; i++) {
					bees[i] = screenWidth + (i * screenWidth / 2);
					Random r1 = new Random();
					Random r2 = new Random();
					Random r3 = new Random();

					// Set three different bees, each on a random position on Y
					beesY[0][i] = r1.nextFloat() * screenHeight;
					beesY[1][i] = r2.nextFloat() * screenHeight;
					beesY[2][i] = r3.nextFloat() * screenHeight;

					// Set collapse circle on enemies
					enemy1Circle[i] = new Circle();
					enemy2Circle[i] = new Circle();
					enemy3Circle[i] = new Circle();
				}

				score = 0;
				// Make the bird jump 20 points
				state = 1;
			}
		}
		// If status = 0
		else {
			text.draw(batch, "Tap to screen to start!", screenWidth / 5, screenHeight / 2);
			if (Gdx.input.justTouched()) {
				score = 0;
				state = 1;
			}
		}

		// Enemy hitbox
		birdCircle.set(birdX + birdWidth / 2, birdY + birdHeight / 2, birdWidth / 2);
		for (int i = 0; i < numberOfBees; i++) {
			enemy1Circle[i].set(bees[i] + birdWidth / 2, beesY[0][i] + birdHeight / 2, birdWidth / 2);
			enemy2Circle[i].set(bees[i] + birdWidth / 2, beesY[1][i] + birdHeight / 2, birdWidth / 2);
			enemy3Circle[i].set(bees[i] + birdWidth / 2, beesY[2][i] + birdHeight / 2, birdWidth / 2);

			// Check if the bird collapsible circle overlaps with an enemy hitbox circle
			if (Intersector.overlaps(birdCircle, enemy1Circle[i]) || Intersector.overlaps(birdCircle, enemy2Circle[i])
			 || Intersector.overlaps(birdCircle, enemy3Circle[i])) {
				state = 2;
			}
		}

		batch.end();
	}
	
	@Override
	public void dispose () {

	}
}
