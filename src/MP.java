import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;


import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class MP  extends JPanel{
	final static int width = 800;
	final static int height = 750;
	final JFXPanel fxPanel = new JFXPanel();
	private MediaPlayer mp;	
	private Media media;
	private File file;
	
	public MP() {
		file = new File("/Users/danhho/Dropbox/PC/Music/BIGBANG - IF YOU.mp3");
		media = new Media(file.toURI().toString());
		mp = new MediaPlayer(media);
		mp.setAutoPlay(true);
//		mp.play();
//		mp.play();
//		
		System.out.println(mp.getStatus());
//		mp.setOnError(()-> System.out.println("Error: " + mp.getError().toString()));
//		if(mp.getStatus() == MediaPlayer.Status.READY) {
//			System.out.println("ready");
//			mp.play();
//		}
//		mp.setOnReady(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				System.out.println(media.getDuration().toSeconds());
//				mp.play();
//			}
//			
//		});

//		mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
//				// TODO Auto-generated method stub
////				slider.setValue(newValue);
//			}
//			
//		});
		
	}
	public void play() {
		mp.play();
	}


	public static void main(String[] args) {

		JFrame f = new JFrame();
		f.setTitle("Danh's MP");
		f.setPreferredSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		MP mp = new MP();
//		mp.play();
		f.add(mp);
		f.pack(); // need this to be visible
		f.setVisible(true);
	}
}
