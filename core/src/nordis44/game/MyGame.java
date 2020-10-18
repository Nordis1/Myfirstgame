package nordis44.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.bcel.internal.generic.SWITCH;

import java.util.Iterator;

public class MyGame extends ApplicationAdapter {
    OrthographicCamera camera;
    SpriteBatch batch;// объект класса который отображает объуктов на экране
    BitmapFont font; // для отображения текста на экране
    Texture dropimage;
    Texture buckedimage,gameover;
    Sound dropSound;
    Music rainMusic,overMusic;
    Rectangle bucked;
    int count = 0;
    int Totalcounter = 0;
    int speedrop = 200;
    private State state;

    Vector3 touchPos;
    Array<Rectangle> rainDrops;
    long lastDropTime;

    @Override
    public void create() {
                font = new BitmapFont();
                touchPos = new Vector3();

                camera = new OrthographicCamera();
                camera.setToOrtho(false, 800, 480);

                batch = new SpriteBatch();
                dropimage = new Texture("droplet.png");
                buckedimage = new Texture("bucket.png");

                dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
                rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));

                rainMusic.setLooping(true);
                rainMusic.play();

                bucked = new Rectangle();
                bucked.x = 800 / 2 - 64 / 2;
                bucked.y = 20;
                bucked.width = 64;
                bucked.height = 64;


                rainDrops = new Array<Rectangle>();
                spawnRainDrops();

/*                font = new BitmapFont();
                touchPos = new Vector3();

                camera = new OrthographicCamera();
                camera.setToOrtho(false,800,480);

                batch = new SpriteBatch();
                gameover = new Texture("game_over.jpg");
                overMusic = Gdx.audio.newMusic(Gdx.files.internal("over.mp3"));*/

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();//Сначала нужно запустить батч только поле начинать прорисовку всего!!!
        font.draw(batch, "Count " + Totalcounter, 0, 470);
        batch.draw(buckedimage, bucked.x, bucked.y); // прорисовка ведра
        for (Rectangle raindrop : rainDrops) { // прорисовка капли
            batch.draw(dropimage, raindrop.x, raindrop.y);
        }
        batch.end(); // это метод должен быть коенчным для прорисовки.


        if (Gdx.input.isTouched()) { // если было прикосновение к экрану
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucked.x = (int) touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucked.x -= 200 * Gdx.graphics.getDeltaTime();// Если нажата клавиша влево или вправо
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucked.x += 200 * Gdx.graphics.getDeltaTime();

        if (bucked.x < 0) bucked.x = 0;//проверка если ведро ушло дальше экрана
        if (bucked.x > 800 - 64) bucked.x = 800 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRainDrops(); // Если прошло опроделённое время то делаем ещё каплю

        Iterator<Rectangle> iterator = rainDrops.iterator(); // создаём движение капли вниз и если капля ниже определённого уровня
        while (iterator.hasNext()) {                      // удаляем её.
            Rectangle raindrop = iterator.next();
            raindrop.y -= speedrop * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) iterator.remove();
            if (raindrop.overlaps(bucked)) {
                dropSound.play();
                iterator.remove();
                count++;
                Totalcounter++;
            }
            if (count == 5) {
                speedrop += 50;
                count = 0;
            }
            if (raindrop.y + 64 < 0) {

                dispose();
            }
        }
    }

    public void spawnRainDrops() { // метод для добавления капли в рандомном месте по оси x
        Rectangle rainDrop = new Rectangle();
        rainDrop.x = MathUtils.random(0, 800 - 64);
        rainDrop.y = 480;
        rainDrop.width = 64;
        rainDrop.height = 64;
        rainDrops.add(rainDrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void dispose() {
        batch.dispose();
        dropimage.dispose();
        buckedimage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        font.dispose();
    }

}
