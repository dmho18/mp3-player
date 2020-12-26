import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Counter {
	final int TIMEVALUE = 1000;

	private Timer t;
	private int sec = 0;
	private int min = 0;
	private String songTime;
	private int songLen = 0;
	private MusicPlayer mplayer;

	public Counter(MusicPlayer mplayer) {
		this.mplayer = mplayer;

		ActionListener lis = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!reachEnd()) {
					if (mplayer.mp == null)
						return;
					int t = (int) mplayer.mp.getCurrentTime().toSeconds();
					min = t / 60;
					sec = t % 60;

					if (mplayer.notDrag) {
						mplayer.timeSlider.setValue((min * 60 + sec) * TIMEVALUE / songLen);
					}
					if (mplayer.reader != null) {
						mplayer.reader.set(mplayer.lyric, mplayer.mp);
					}
					setTimeText(min, sec);

				} else {
					reset();
					mplayer.mp.stop();
					mplayer.setButton(mplayer.play, 50, mplayer.play.getX(), mplayer.play.getY(), "play.png");
				}
			}

		};
		;
		t = new Timer(990, lis);
	}

	public void start() {
		if (mplayer.nowPlaying != null) {
			songLen = mplayer.nowPlaying.getLength();
			int t = (int) mplayer.mp.getCurrentTime().toSeconds();
			min = t / 60;
			sec = t % 60;
			songTime = String.format("%02d", songLen / 60) + ":" + String.format("%02d", songLen % 60);
			mplayer.lyric.setText("");
		}
		t.start();
	}

	public void stop() {
		t.stop();
	}

	public void reset() {
		t.stop();
		setTimeText(0, 0);
		mplayer.timeSlider.setValue(0);

	}

	public void setLen(int len) {
		songLen = len;
	}

	public void setTimeText(int minute, int second) {
		min = minute;
		sec = second;
		String m = String.format("%02d", min);
		String s = String.format("%02d", sec);
		mplayer.time.setText(m + ":" + s + " / " + songTime);

	}

	public boolean reachEnd() {
		if (songLen > 0)
			return min * 60 + sec == songLen - 1;
		else
			return false;

	}
}
