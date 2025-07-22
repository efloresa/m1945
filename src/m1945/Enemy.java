package m1945;

import java.util.Random;

class Enemy extends Sprite {

	private int type,state,deltaX,deltaY;

	public void setState(int state) {
		this.state=state;
	}

	public int getState(int state) {
		return state;
	}

	public void setType(int type) {
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public void doMovement() {

		Random random = new java.util.Random();
		
		// Los enemigos de tipo 2 cambiaran su trayectoria
		// al alcanzar una posici�n determinada (pos. 50)
		if (type == 2 && getY() > 50 && deltaX == 0) {
			if ((Math.abs(random.nextInt()) % 2) + 1 == 1) {
				deltaX=2;	
			} else {
				deltaX=-2;	
			}
		}
		
		// movemos la nave
		setX(getX()+deltaX);
		setY(getY()+deltaY);

	}

	public void init(int xhero) {
		deltaY=3;
		deltaX=0;

		if (type == 1) {
			if (xhero > getX()) {
				deltaX=2;
			} else {
				deltaX=-2;
			}
		}
	}

	// Sobrecarga del m�todo draw de la clase Sprite
	public void draw (javax.microedition.lcdui.Graphics g) {
		selFrame(type);
		// llamamos al m�todo 'draw' de la clase padre (Sprite)
		super.draw(g);
	}

	public Enemy(int nFrames) {
		super(nFrames);
	}
}