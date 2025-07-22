package ec.gob.epmtg.m1945;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import android.graphics.Paint;

import java.util.*;
import java.io.*;

public class M1945 extends MIDlet implements CommandListener {

 private Command exitCommand, playCommand, endCommand;
 private Display display;
 private SSCanvas screen;

 public M1945() {
	display=Display.getDisplay(this);
	exitCommand = new Command("Salir",Command.SCREEN,2);
	playCommand = new Command("Jugar",Command.CANCEL,2);
	endCommand	= new Command("Salir",Command.SCREEN,2);

	screen=new SSCanvas();

	screen.addCommand(playCommand);
	screen.addCommand(exitCommand);
	screen.setCommandListener(this);
 }

 public void startApp() throws MIDletStateChangeException {
	display.setCurrent(screen);
 }

 public void pauseApp() {}

 public void destroyApp(boolean unconditional) {}

 public void commandAction(Command c, Displayable s) {

	if (c == exitCommand) {
		if (screen.isPlaying()) {
			screen.quitGame();
		} else {
			destroyApp(false);
			notifyDestroyed();
		}
	}

	if (c == playCommand && !screen.isPlaying()) {
		// Play!!!
		new Thread(screen).start();
	}

 }

}

class SSCanvas extends Canvas implements Runnable {

 private int score,sleepTime,cicle,lives,shield;
 private int indice_in, indice, xTiles, yTiles;
 private boolean playing,fireOn=false;
 private boolean done;
 private int deltaX,deltaY;
 private Hero hero=new Hero(1);
 private Enemy[] enemies=new Enemy[7];
 private Bullet[] aBullet=new Bullet[7];
 private Sprite intro=new Sprite(1);
 private Explode[] explode=new Explode[7];
 private Sprite[] tile=new Sprite[5];

 // Mapa del juego
 int map[] ={	1,1,1,1,1,1,1,
 				1,1,1,1,1,1,1,
 				1,2,1,1,1,1,1,
				1,1,1,4,1,1,1,
				1,1,1,1,1,1,1,
				1,1,3,1,2,1,1,
				1,1,1,1,1,1,1,
				1,4,1,1,1,1,1,
				1,1,1,1,3,1,1,
				1,1,1,1,1,1,1,
				1,4,1,1,1,1,1,
				1,1,1,3,1,1,1,
				1,1,1,1,1,1,1,
 				1,1,1,1,1,1,1,
 				1,2,1,1,1,1,1,
				1,1,1,4,1,1,1,
				1,1,1,1,1,1,1,
				1,1,3,1,2,1,1,
				1,1,1,1,1,1,1,
				1,4,1,1,1,1,1};


 public SSCanvas() {
	// Cargamos los sprites
	hero.addFrame(1,"/hero.png");
	intro.addFrame(1,"/intro.png");

	// Iniciamos los Sprites
	hero.on();
	intro.on();
 }

 void start() {
	 int i;
	 
	playing = true;
	sleepTime = 50;
	hero.setX(getWidth()/2);
	hero.setY(getHeight()-20);
	deltaX=0;
	deltaY=0;
	cicle=0;
	xTiles=7;
	yTiles=8;
	indice=map.length-(xTiles*yTiles);
	indice_in=0;
	score=0;
	lives=3;
	shield=0;

	// Inicializar enemigos
	for (i=1 ; i<=6 ; i++) {
		enemies[i]=new Enemy(2);
		enemies[i].addFrame(1,"/enemy1.png");
		enemies[i].addFrame(2,"/enemy2.png");
		enemies[i].off();
	}

	// Inicializar balas
	for (i=1 ; i<=6 ; i++) {
		aBullet[i]=new Bullet(2);
		aBullet[i].addFrame(1,"/mybullet.png");
		aBullet[i].addFrame(2,"/enemybullet.png");
		aBullet[i].off();
	}

	// Inicializamos los tiles
	for (i=1 ; i<=4 ; i++) {
		tile[i]=new Sprite(1);
		tile[i].on();
	}

	tile[1].addFrame(1,"/tile1.png");
	tile[2].addFrame(1,"/tile2.png");
	tile[3].addFrame(1,"/tile3.png");
	tile[4].addFrame(1,"/tile4.png");

	// Inicializamos explosiones
	for (i=1 ; i<=6 ; i++) {
		explode[i]=new Explode(6);

		explode[i].addFrame(1,"/explode1.png");
		explode[i].addFrame(2,"/explode2.png");
		explode[i].addFrame(3,"/explode3.png");
		explode[i].addFrame(4,"/explode4.png");
		explode[i].addFrame(5,"/explode5.png");
		explode[i].addFrame(6,"/explode6.png");
	}
}

 void computeEnemies() {
	int freeEnemy,i;
	Random random = new java.util.Random();

	 // Creamos un enemigo cada 20 ciclos
	 if (cicle%20 == 0) {

		 freeEnemy=0;

		// Buscar un enemigo libre
		for (i=1 ; i<=6 ; i++) {
			if (!enemies[i].isActive()) {
				freeEnemy=i;
			}
		}

		// Asignar enemigo si hay una posición libre
		// en el array de enemigos
		if (freeEnemy != 0) {
			enemies[freeEnemy].on();
			enemies[freeEnemy].setX((Math.abs(random.nextInt()) % getWidth()) + 1);
			enemies[freeEnemy].setY(0);
			enemies[freeEnemy].setState(1);
			enemies[freeEnemy].setType((Math.abs(random.nextInt()) % 2) + 1);
			enemies[freeEnemy].init(hero.getX());
		}
	 }
	 
	 // Mover los enemigos
	 for (i=1 ; i<=6 ; i++) {
		if (enemies[i].isActive()) {
			enemies[i].doMovement();
		}

		// Mirar si la nave salió de la pantalla
		if ((enemies[i].getY() > getHeight()) || (enemies[i].getY() < 0)) {
			enemies[i].off();
		}
	 }

 }

 void computeBullets() {
	int freeBullet,theEnemy,i,j;

	// Crear disparo del jugador
	freeBullet=0;
	if (fireOn) {
		// Buscar un disparo libre
		for (i=1 ; i<=6 ; i++) {
			if (!aBullet[i].isActive()) {
				freeBullet=i;
			}
		}

		if (freeBullet !=0) {
			aBullet[freeBullet].on();
			aBullet[freeBullet].setX(hero.getX());
			aBullet[freeBullet].setY(hero.getY()-10);
			aBullet[freeBullet].setOwner(1);
		}
	}

	// Crear disparo de enemigos
	freeBullet=0;
	theEnemy=0;

	for (i=1 ; i<=6 ; i++) {
		if (enemies[i].getType() == 1 && enemies[i].isActive() && enemies[i].getY() > getHeight()/2 && enemies[i].getY() < (getHeight()/2)+5) {
			// Buscar un disparo libre
			for (j=1 ; j<=6 ; j++) {
				if (!aBullet[j].isActive()) {
					freeBullet=j;
					theEnemy=i;
				}
			}

			if (freeBullet !=0) {
				aBullet[freeBullet].on();
				aBullet[freeBullet].setX(enemies[theEnemy].getX());
				aBullet[freeBullet].setY(enemies[theEnemy].getY()+10);
				aBullet[freeBullet].setOwner(2);
			}
		}
	}

	// Mover los disparos
	for (i=1 ; i<=6 ; i++) {
		if (aBullet[i].isActive()) {
			aBullet[i].doMovement();
		}

		// Mirar si el disparo salió de la pantalla
		if ((aBullet[i].getY() > getHeight()) || (aBullet[i].getY() <= 0)) {
			aBullet[i].off();
		}
	}

 }

 void computeExplodes() {
	 int i;

	for (i=1 ; i<=6 ; i++) {
		explode[i].doMovement();
	}
 }

 void doScroll() {
	// movimiento del scenario (scroll)
	indice_in+=2;
	if (indice_in>=32) {
		indice_in=0;
		indice-=xTiles;
	}

	if (indice <= 0) {
		// si llegamos al final, empezamos de nuevo.
		indice=map.length-(xTiles*yTiles);
		indice_in=0;
	}
 }

 void createExplode(int posx, int posy) {

	 int freeExplode,i;

	 freeExplode=0;

	// Buscar una explosión libre
	for (i=1 ; i<=6 ; i++) {
		if (!explode[i].isActive()) {
			freeExplode=i;
		}
	}

	if (freeExplode !=0) {
		explode[freeExplode].setState(1);
		explode[freeExplode].on();
		explode[freeExplode].setX(posx);
		explode[freeExplode].setY(posy);
	}

 }

 void checkCollide() {

	int i,j;
	boolean collision;

	collision=false;

	// Colisión heroe-enemigo
	for (i=1 ; i<=6 ; i++) {
		if (hero.collide(enemies[i]) && enemies[i].isActive() && shield == 0) {
			createExplode(hero.getX(),hero.getY());
			createExplode(enemies[i].getX(),enemies[i].getY());
			enemies[i].off();
			collision=true;
		}
	}

	// Colisión heroe-disparo
	for (i=1 ; i<=6 ; i++) {
		if (aBullet[i].isActive() && hero.collide(aBullet[i]) && aBullet[i].getOwner() != 1  && shield == 0) {
			createExplode(hero.getX(),hero.getY());
			aBullet[i].off();
			collision=true;
		}
	}

	// colisión enemigo-disparo
	for (i=1 ; i<=6 ; i++) {
		if (aBullet[i].getOwner() == 1 && aBullet[i].isActive()) {
			for (j=1 ; j<=6 ; j++) {
				if (enemies[j].isActive()) {
					if (aBullet[i].collide(enemies[j])) {
						createExplode(enemies[j].getX(),enemies[j].getY());
						enemies[j].off();
						aBullet[i].off();
						score+=10;
					}
				}
			}
		}
	}

	if (collision == true) {
		lives--;

		// poner heroe al estado inicial
		hero.setX(getWidth()/2);
		hero.setY(getHeight()-20);

		// Durante 20 ciclos nuestra nave será inmune
		shield=20;

		if (lives <= 0) {
			playing=false;
		}
	}

	if (shield > 0)
		shield--;

 }

 void computePlayer() {
	 // actualizar posición del avión
	 if (hero.getX()+deltaX>0 && hero.getX()+deltaX<getWidth() && hero.getY()+deltaY>0 && hero.getY()+deltaY<getHeight()) {
	 	hero.setX(hero.getX()+deltaX);
	 	hero.setY(hero.getY()+deltaY);
 	}
 }

 void quitGame() {
	playing = false;
 }

 boolean isPlaying() {
	return playing;
 }

 public void run() {
	start();

 	while (playing) {

	 	// Actualizar fondo de pantalla
	 	doScroll();

	 	// Actualizar posición del jugador
	 	computePlayer();

	 	// Actualizar posición de los enemigos
	 	computeEnemies();

	 	// Actualizar balas
	 	computeBullets();

	 	// Actualizar explosiones
	 	computeExplodes();

	 	// Comprobar colisiones
	 	checkCollide();

		// Contador de ciclos
		cicle++;

		// Actualizar pantalla
		repaint();
		serviceRepaints();

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			System.out.println(e.toString());
		}
	}

	// Repintamos la pantalla
	// para mostrar pantalla de presentación
	repaint();
	serviceRepaints();
 }

 public void keyReleased(int keyCode) {
	int action=getGameAction(keyCode);

	switch (action) {

		case FIRE:
			fireOn=false;
			break;
		case LEFT:
			deltaX=0;
			break;
		case RIGHT:
			deltaX=0;
			break;
		case UP:
			deltaY=0;
			break;
		case DOWN:
			deltaY=0;
  			break;
	}
 }

 public void keyPressed(int keyCode) {

	int action=getGameAction(keyCode);

	switch (action) {

		case FIRE:
			fireOn=true;
			break;
		case LEFT:
			deltaX=-5;
			break;
		case RIGHT:
			deltaX=5;
			break;
		case UP:
			deltaY=-5;
			break;
		case DOWN:
			deltaY=5;
  			break;
	}
 }

 public void paint(Graphics g) {

	int x=0,y=0,t=0;
	int i,j;

	g.setColor(255,255,255);
	g.fillRect(0,0,getWidth(),getHeight());
	g.setColor(200,200,0);

	g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));

	if (playing == false) {
		intro.setX(getWidth()/2);
		intro.setY(getHeight()/2);
		intro.draw(g);
	} else {
		// Dibujar fondo
		for (i=0 ; i<yTiles ; i++) {
			for (j=0 ; j<xTiles ; j++) {
				t=map[indice+(i*xTiles+j)];
				// calculo de la posición del tile
				x=j*32;
				y=(i-1)*32+indice_in;

				// dibujamos el tile
				tile[t].setX(x);
				tile[t].setY(y);
				tile[t].draw(g);
			}
		}

		// Dibujar enemigos
		for (i=1 ; i<=6 ; i++) {
			if (enemies[i].isActive()) {
				enemies[i].setX(enemies[i].getX());
				enemies[i].setY(enemies[i].getY());
				enemies[i].draw(g);
			}
		}

		// Dibujar el jugador
		if (shield == 0 || shield%2 == 0) {
			hero.setX(hero.getX());
			hero.setY(hero.getY());
			hero.draw(g);
		}

		// Dibujar disparos
		for (i=1 ; i<=6 ; i++) {
			if (aBullet[i].isActive()) {
				aBullet[i].setX(aBullet[i].getX());
				aBullet[i].setY(aBullet[i].getY());
				aBullet[i].draw(g);
			}
		}

		// Dibujar explosiones
		for (i=1 ; i<=6 ; i++) {
			if (explode[i].isActive())
				explode[i].draw(g);
		}
		
		g.drawString(" "+score,getWidth()-20,20, Paint.Align.CENTER|Graphics.BOTTOM);
		g.drawString(" "+lives,20,20, Paint.Align.CENTER|Graphics.BOTTOM);
	}
 }

}
