package m1945;

class Hero extends Sprite {

	private int state;

	public void setState(int state) {
		this.state=state;
	}

	public int getState(int state) {
		return state;
	}

	public Hero(int nFrames) {
		super(nFrames);
	}
}