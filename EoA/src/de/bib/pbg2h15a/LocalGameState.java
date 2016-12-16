package de.bib.pbg2h15a;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author pbg2h15asu
 * @author pbg2h15aza: timer & gamestate
 * @author pbg2h15awi: timer
 */

public class LocalGameState extends GameState{

	private float speed = 2.5f;

	private BitmapFont font;
	private BitmapFont font_countdown;
	
	private SpriteBatch batch;
	
	private Texture texture_player;
	private Texture texture_background;
	private Texture texture_pillar;
	private Texture texture_wall;
	
	private Sprite sprite_player;
	
	private Player[] players;
	
	private final float FIELD_START_X = 80f;
	private final float FIELD_START_Y = 32f;
	private final float FIELD_END_X = 528f;
	private final float FIELD_END_Y = 352f;
	
	private final float COLLISION_OFFSET = 1f;
	
	private Sprite[][] field;
	private List<Sprite> collision_objects;
	
	private Timer timer = new Timer(6);
	
	protected LocalGameState(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
    	batch = new SpriteBatch();
    	
    	collision_objects = new LinkedList<Sprite>();

    	font = new BitmapFont();
    	font.setColor(Color.BLACK);
    	font_countdown = new BitmapFont();
    	font_countdown.setColor(Color.FIREBRICK);
    	font_countdown.getData().setScale(2);
    	
    	texture_player = new Texture(Gdx.files.internal("data/red32x32.jpg"));
    	sprite_player = new Sprite(texture_player);
    	sprite_player.setPosition(FIELD_START_X, FIELD_START_Y);

    	/**
    	 * texture not final !
    	 */
    	texture_background = new Texture(Gdx.files.internal("data/grey32x32.jpg"));
    	texture_pillar = new Texture(Gdx.files.internal("data/brown32x32.jpg"));
    	texture_wall = new Texture(Gdx.files.internal("data/orange32x32.jpg"));
    	
    	field = setupField(17, 13);
		
	}

	@Override
	public void update(float dt) {
		if(timer.isFinished()){
			float posx = sprite_player.getX();
	    	float posy = sprite_player.getY();
	    	
	    	if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	    		sprite_player.translateX(-speed);
		    	
		    	if(sprite_player.getX() < FIELD_START_X)
		    		sprite_player.setPosition(FIELD_START_X, sprite_player.getY());
		    	if(collision(sprite_player, collision_objects, COLLISION_OFFSET))
		    		sprite_player.setPosition(posx, posy);
	    	}
	    	
	    	if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	    		sprite_player.translateX(speed);
		    	
		    	if(sprite_player.getX() > FIELD_END_X)
		    		sprite_player.setPosition(FIELD_END_X, sprite_player.getY());
			    if(collision(sprite_player, collision_objects, COLLISION_OFFSET))
		    		sprite_player.setPosition(posx, posy);
	    	}
		    
		    posx = sprite_player.getX();
		    
	    	if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
	    		sprite_player.translateY(-speed);
	    		
		    	if(sprite_player.getY() < FIELD_START_Y)
		    		sprite_player.setPosition(sprite_player.getX(), FIELD_START_Y);
		    	if(collision(sprite_player, collision_objects, COLLISION_OFFSET))
		    		sprite_player.setPosition(posx, posy);
	    	}
	    	
	    	if(Gdx.input.isKeyPressed(Input.Keys.UP)){
		    	sprite_player.translateY(speed);
		    	
		    	if(sprite_player.getY() > FIELD_END_Y)
		    		sprite_player.setPosition(sprite_player.getX(), FIELD_END_Y);
			    if(collision(sprite_player, collision_objects, COLLISION_OFFSET))
		    		sprite_player.setPosition(posx, posy);
	    	}
		}else{
			timer.update(dt);
		}
	}

	@Override
	public void render() {
    	Gdx.gl.glClearColor(1, 1, 1, 1);
    	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	String pos = "";
    
    	pos = "x/y: " + sprite_player.getX() + " - " + (sprite_player.getX()+sprite_player.getWidth()) + " / " + sprite_player.getY() + " - " + (sprite_player.getY()+sprite_player.getHeight());
    	
    	
    	batch.begin();
    	drawField(field, 17, 13);
    	font.draw(batch, pos, 80, 16);
    	sprite_player.draw(batch);
    	if(!timer.isFinished()){
    		int time = (int)timer.getTime();
    		font_countdown.draw(batch, ""+time, Gdx.graphics.getWidth()/2 - 8, Gdx.graphics.getHeight()/2-20);
    	}
    	batch.end();
		
	}

	@Override
	public void dispose() {
    	batch.dispose();
    	texture_player.dispose();
	}

	private Sprite[][] setupField(int width, int height){
    	
    	Sprite[][] newField = new Sprite[height][width];
    	
    	for(int i=0;i<height;i++){
    		for(int j=0;j<width;j++){
    			int posx = 80 - 32 + 32 * j;
    			int posy = 0 + 32 * i;
    			if(i == 0 || j == 0 || i == height-1 || j == width-1 || ((i % 2) == 0 && (j % 2) == 0)){
    				Sprite pillar = new Sprite(texture_pillar);
    				pillar.setPosition(posx, posy);
    				newField[i][j] = pillar;
    				collision_objects.add(pillar);
				}else{
    				Sprite background = new Sprite(texture_background);
    				background.setPosition(posx, posy);
    				newField[i][j] = background;
				}
    		}
    	}
    	
    	return newField;
    }

    private void drawField(Sprite[][] sprites, int width, int height) {
    	for(int i=0;i<height;i++){
    		for(int j=0;j<width;j++){
    			Sprite s = sprites[i][j];
    			s.draw(batch);
    		}
    	}
	}
    
    private boolean collision(Sprite s1, List<Sprite> s2, float offset){

    	boolean collision = false;
    	CollisionDetector cd = new CollisionDetector(sprite_player, (int) COLLISION_OFFSET);
    	
    	for(Sprite s : s2){
    		if(cd.collidesWith(s))
    			collision = true;
    	}
    	
    	return collision;
    }
}
