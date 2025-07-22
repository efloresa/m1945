package ec.gob.epmtg.m1945;

class Bullet extends Sprite {
	private int owner;

	public Bullet(int nFrames) {
		super(nFrames);
	}

	public void setOwner(int owner) {
		this.owner=owner;
	}

	public int getOwner() {
		return owner;
	}

	public void doMovement() {
		// si owner = 1 el disparo es nuestro
		// si no, es del enemigo
		if (owner == 1) {
			setY(getY()-6);
		} else {
			setY(getY()+6);
		}
	}

	// Sobrecarga del m�todo draw de la clase Sprite
        @Override
	public void draw (javax.microedition.lcdui.Graphics g) {
		selFrame(owner);
		// llamamos al m�todo 'draw' de la clase padre (Sprite)
		super.draw(g);
	}

}
