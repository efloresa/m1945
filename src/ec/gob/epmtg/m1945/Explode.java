package ec.gob.epmtg.m1945;

class Explode extends Sprite {

	private int state;

	public Explode(int nFrames) {
		super(nFrames);
		state=1;
	}

	public void setState(int state) {
		this.state=state;
	}

	public int getState() {
		return state;
	}

	public void doMovement() {
		state++;

		if (state > super.frames())
			super.off();
	}

	// Sobrecarga del método draw de la clase Sprite
	public void draw (javax.microedition.lcdui.Graphics g) {
		selFrame(state);
		// llamamos al método 'draw' de la clase padre (Sprite)
		super.draw(g);
	}
	
}
