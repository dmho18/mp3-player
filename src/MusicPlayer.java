
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.SliderUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

//import entagged.audioformats.AudioFile;
//import entagged.audioformats.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.*;

import org.w3c.dom.events.MouseEvent;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;
import javafx.beans.Observable;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MusicPlayer extends JPanel implements ActionListener {
	final static int width = 800;
	final static int height = 750;
	final String LIBPANEL = "Library";
	final String PLAYPANEL = "Playlist";
	final int TIMEVALUE = 1000;

	private Media media;
	private MediaPlayer mp;
	private JTextField tf;

	private JButton play;
	private JButton pause;
	private JButton stop;
	private JButton open;
	private JButton save;
	private JButton next;
	private JButton prev;
	private JButton add;

	private JLabel title;
	private JLabel time;
	private JLabel lyric;
	private Library lib;
	private MyTree playlist;
	private JComboBox<String> organize;

	private MouseListener mlis;
	private boolean start = false;
	private boolean autoPlay = true;

	private AdvancedPlayer player = null;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private File file;
	private long length;
	private long pauseLocation = 0;
	private Song nowPlaying = null;
	private Counter c;
	private JSlider timeSlider;
	private JSlider volume;
	private int num = 0;
	private boolean notDrag = true;
	private SrtReader reader;

	final JFXPanel fxPanel = new JFXPanel();

	public MusicPlayer()  {
//		reader = new SrtReader("rather be.srt");
		try {
			create();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wire();
	}

	private void create() throws JavaLayerException, IOException {


		file = new File("/Users/danhho/Dropbox/PC/Music/BIGBANG - IF YOU.mp3");
		media = new Media(file.toURI().toString());
		mp = new MediaPlayer(media);
//		mp.setAutoPlay(true);
		mp.play();
		System.out.println("status: "+mp.getStatus());
		lib = new Library("library.txt", Color.lightGray);
		playlist = new MyTree();
		setUI();

	}

	private void setUI() throws IOException {
		this.setLayout(null);

		// control panel - bot
		JPanel controlPanel = new JPanel(null);
		controlPanel.setOpaque(true);

		// ImageIcon playIcon = new ImageIcon(img);
		// play = new JButton(playIcon);
		// BufferedImage PlayButtonImage = ImageIO.read(new File("play.png"));

		pause = new JButton("pause");
		play = new JButton();
		setButton(play, 400, 50, "play.png");

		stop = new JButton("stop");
		next = new JButton("next");
		prev = new JButton("previous");
		title = new JLabel();
		set(title, Color.BLACK, new Dimension(250, 20), 20, 90);
		time = new JLabel();
		set(time, Color.BLACK, new Dimension(100, 20), 680, 90);
		timeSlider = new JSlider(0, TIMEVALUE, 0);
		volume = new JSlider(JSlider.VERTICAL, 0, 1, 1 / 2);
		volume.setMajorTickSpacing(1 / 10);
		volume.setBounds(750, 20, 20, 70);

		timeSlider.setBounds(10, 10, 780, 10);
		timeSlider.setBackground(Color.RED); // doesn't work
		timeSlider.setForeground(Color.blue);
		timeSlider.setPaintTrack(true);
		
		pause.setBounds(340, 50, 50, 50);
		prev.setBounds(280, 50, 50, 50);
		stop.setBounds(460, 50, 50, 50);
		next.setBounds(520, 50, 50, 50);

		// improve this later
		class MyMouseListener extends MouseAdapter {

			public void mousePressed(MouseEvent e) {
				changeState();

			}

			public void mouseDragged(MouseEvent e) {
				changeState();

			}

			public void mouseReleased(MouseEvent e) {
				notDrag = true;

			}

		}
		timeSlider.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				changeState();
				notDrag = true;

			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				changeState();
				notDrag = true;
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				notDrag = true;
				// System.out.println("mouse is not on the bar, auto-move");

			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
		timeSlider.addMouseMotionListener((MouseMotionListener) new MouseMotionListener() {

			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				changeState();

			}

			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		volume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mp.setVolume(volume.getValue());
				System.out.println("get vol: " + mp.getVolume());

			}

		});

		controlPanel.add(volume);
		controlPanel.add(timeSlider);
		controlPanel.add(prev);
		controlPanel.add(play);
		controlPanel.add(pause);
		controlPanel.add(stop);
		controlPanel.add(next);
		controlPanel.add(title);
		controlPanel.add(time);
		// set(controlPanel, this.getBackground(), new Dimension(590, 100), 210, 600);
		set(controlPanel, this.getBackground(), new Dimension(800, 150), 0, 600);
		add(controlPanel);

		// top panel
		JPanel top = new JPanel();
		tf = new JTextField(20);
		open = new JButton("Open");
		open.setName("Open");
		save = new JButton("Save");
		add = new JButton("Add Playlist");
		top.add(tf);
		top.add(open);
		top.add(save);
		top.add(add);
		set(top, this.getBackground(), new Dimension(590, 100), 210, 0);
		add(top);

		// display panel
		JPanel display = new JPanel(null);
		set(display, Color.BLACK, new Dimension(590, 500), 210, 100);
		lyric = new JLabel("something");
		set(lyric, Color.white, new Dimension(400, 20), 60, 450);
		// reader.set(lyric);
		display.add(lyric);
		//
		 JFXPanel pan = new JFXPanel();
		// Group root = new Group();
		// Scene scene = new Scene(root, 590, 500);
		// create media player
		// MediaView mv = new MediaView();
		// mv.setMediaPlayer(mp);
		// mp.setAutoPlay(true);

		// create mediaView and add media player to the viewer
		// ((Group) scene.getRoot()).getChildren().add(mv);
		// pan.setScene(scene);
		// display.add(pan);

		// title = new JLabel();
		// set(title, Color.WHITE, new Dimension(400, 20), 30, 450);
		// time = new JLabel();
		// set(time, Color.white, new Dimension(100, 20), 480, 450);
		// display.add(time);
		// display.add(title);
		add(display);

		// left panel
		JTabbedPane left = new JTabbedPane();
		set(left, Color.LIGHT_GRAY, new Dimension(210, 510), 0, 100);
		left.addTab(LIBPANEL, lib);
		left.addTab(PLAYPANEL, playlist);
		add(left);

		// top left
		JPanel func = new JPanel();
		set(func, null, new Dimension(200, 100), 10, 0);
		func.add(new JLabel("Sort: "));
		organize = new JComboBox<String>(new String[] { "Date", "Title", "Length" });
		func.add(organize);
		organize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> combo = (JComboBox<String>) e.getSource();
				String selected = (String) combo.getSelectedItem();
				if (selected.equals("Date")) {
					System.out.println("Good choice 1!");
				} else if (selected.equals("Title")) {
					System.out.println("Good choice!");
				} else if (selected.equals("Length")) {
					System.out.println("Nice pick, too!");
				}
			}

		});
		add(func);

	}

	private void setButton(JButton but, int x, int y, String path) {
		// "/Users/danhho/Dropbox/CS3B/MusicPlayer/play.png"
		Image img;
		try {
			img = ImageIO.read(new File(path));
			img = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(img);
			but.setIcon(icon);
			// reshape to the shape of image
			but.setContentAreaFilled(false);
			but.setFocusPainted(false);
			but.setBorderPainted(false);
			but.setBounds(x, y, icon.getIconWidth(), icon.getIconWidth());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void changeState() {
		if (mp != null) {
			Duration duration = mp.getMedia().getDuration();
			int time = (int) mp.getCurrentTime().toSeconds();
			notDrag = false;
			c.setTimeText(time / 60, time % 60);
			duration = new Duration(duration.toMillis() * timeSlider.getValue() / TIMEVALUE);
			mp.pause();
			mp.seek(duration);
			mp.play();
		}
	}

	private String add() {
		String text = tf.getText().trim();
		if (!text.isEmpty()) {
			tf.setText("");
		}
		return text;
	}

	private void set(JComponent jc, Color color, Dimension displaySize, int x, int y) {
		if (jc instanceof JButton || jc instanceof JLabel || jc instanceof JPanel || jc instanceof JTabbedPane) {
			if (jc instanceof JLabel) {
				jc.setForeground(color);
			} else
				jc.setBackground(color);
			jc.setBounds(x, y, displaySize.width, displaySize.height);
		}
	}

	class popUp extends JPopupMenu {
		ArrayList<JMenuItem> items;

		public popUp(Song song) {
			// System.out.println(song.getTitle());
			items = new ArrayList<JMenuItem>();

			// items = new ArrayList<JMenuItem>();
			for (Playlist temp : playlist.getPlaylist()) {
				JMenuItem item = new JMenuItem(temp.getName());
				item.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(java.awt.event.MouseEvent e) {
					}

					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
						// TODO Auto-generated method stub
						JMenuItem item = (JMenuItem) e.getSource();
						try {
							playlist.addSong(song, item.getText());
							System.out.println(item.getText() + " was clicked!");
							;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					@Override
					public void mouseReleased(java.awt.event.MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseEntered(java.awt.event.MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseExited(java.awt.event.MouseEvent e) {
						// TODO Auto-generated method stub

					}

				});
				items.add(item);
				add(item);
				// item.addMouseListener(lis);

			}
			// for(JMenuItem temp:items) {
			// temp.addMouseListener(lis);
			// }
		}

		public void addItem(String name) {
			JMenuItem item = new JMenuItem(name);
			items.add(item);
			add(item);
			// item.addMouseListener(lis);
			this.updateUI();
		}
	}

	private void wire() {
		c = new Counter();

		play.addActionListener(this);
		pause.addActionListener(this);
		stop.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		next.addActionListener(this);
		prev.addActionListener(this);
		add.addActionListener(this);

		mlis = new MouseListener() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// to prevent not playing while clicking for pop up
				if (!SwingUtilities.isRightMouseButton(e)) {
//					Song song = (Song) e.getSource();
					nowPlaying = (Song) e.getSource();

					num = nowPlaying.getNum();
					String path = nowPlaying.getPath();
					
					System.out.println("this num: " + num +" "+ path);

					// stop(); // stop must be before file
					file = new File(path);
//					nowPlaying = 
					// if(mp != null && mp.getStatus() == Status.PLAYING) {
					// mp.stop();
					// }
					// has this func in play();
					// lib.whiteExcept(num, Color.BLUE);
					c.reset();
					playFile();
					// play();
				}
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (e.getSource() instanceof JLabel) {
					JLabel l = (JLabel) e.getSource();
					l.setForeground(Color.CYAN);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				// JLabel l = (JLabel) e.getSource();
				if (e.getSource() instanceof Song) {
					Song song = (Song) e.getSource();
					String path = "jj";
					if (nowPlaying != null) {
						path = nowPlaying.getPath();
					}
					if (song.getPath().equals(path)) {
						song.setForeground(Color.blue);
					} else {
						song.setForeground(Color.WHITE);
					}

				}
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.isPopupTrigger()) {
					Song song = (Song) e.getSource();
					// System.out.println(song.getTitle());
					doPop(e, song);
				}
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.isPopupTrigger()) {
					Song song = (Song) e.getSource();
					// System.out.println(song.getTitle());

					doPop(e, song);
				}
			}

			private void doPop(java.awt.event.MouseEvent e, Song song) {
				popUp menu = new popUp(song);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}

		};

		ArrayList<Song> songList = lib.getSongList();
		for (Song temp : songList) {
			temp.addMouseListener(mlis);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton but = (JButton) e.getSource(); // casting
		System.out.println(but.getText() + " clicked!");
		// tf.setText(tf.getText() + but.getText());

		if (but.equals(play)) {

			if (lib.getLength() > 0 && num == 0 && !start) {
				file = new File(lib.getSong(num).getPath());
				start = true;
				playFile();
				// System.out.println("loading 1st song " + lib.getSong(num).getPath());
			} else {
				mp.play();
			}
		} else if (but == pause) {
			c.stop();
			// pause();
			if (mp != null) {
				mp.pause();
			}

		} else if (but == stop) {
			if (mp != null) {
				c.reset();
				mp.stop();
			}
			// c.reset();
			// stop();
		} else if (but == open) {
			System.out.println("do nothing for now...");
			// String path = setChooser(open);
			// stop(); // stop must be before file
			// file = new File(path);
			// play();
		} else if (but == save) {
			String path = setChooser(save);
			try (FileWriter fw = new FileWriter("library.txt", true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(path);
				// add to the main library
				lib.add(path);

			} catch (IOException e1) {
			}
		} else if (but == next) {
			playNext();
		} else if (but == prev) {
			playPrev();
		} else if (but == add) {
			String name = add();
			try {
				playlist.addPlaylist(name);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	private void playPrev() {
		if (num - 1 < 0) {
			System.out.println("No previous song");
			return;
		}
		// if(mp != null && mp.getStatus() == Status.PLAYING) {
		// mp.stop();
		// }
		// stop();
		num--;
		c.reset();
		file = new File(lib.getSong(num).getPath());
		playFile();
		// play();
	}

	private void playNext() {

		if (num + 1 >= lib.getLength()) {
			System.out.println("No next song");
			autoPlay = false;
			return;
		}
		// stop();
		// if(mp != null && mp.getStatus() == Status.PLAYING) {
		// mp.stop();
		// }
		if (num >= 0) {
			num++;
		}
		c.reset();
		System.out.println("current num: " + num);
		file = new File(lib.getSong(num).getPath());
		playFile();
		// play();
	}

	private void stop() {
		try {
			System.out.println(pauseLocation);
			if (pauseLocation > 0) {
				player.close();
				bis.close();
				fis.close();
				pauseLocation = -1;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void pause() {
		try {
			pauseLocation = bis.available();
			player.close();
			System.out.println(pauseLocation);
			int min = (int) (pauseLocation / 1000000);
			System.out.println(min + ":" + (int) ((pauseLocation - min * 1000000) / 10000 - 2));
			bis.close();
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void playFile() {
		// for just when open the window
		// it automatically choose the first song assuming the library is not empty

		// search to highlight playing song
		highlightSong();

		// title.setFont(Font.ITALIC);
		title.setText(nowPlaying.title);
		// first open or not pause
		// if (mp == null || mp.getStatus() != Status.PAUSED) {
		// will reset everything here
		if (mp != null && mp.getStatus() == Status.PLAYING) {
			mp.stop();
		}
		start = true;
		media = new Media(file.toURI().toString());
		mp = new MediaPlayer(media);
		System.out.println("in playFile: "+media.getDuration().toSeconds());
		c.setLen((int) mp.getMedia().getDuration().toSeconds());
		c.start();

		mp.play();

		// }
		// else {
		// mp.play();
		// }

	}

	private void highlightSong() {
		String newPath = file.getPath();
		num = lib.search(newPath);
		if (num == -1) {
			// removing open so I'm considering moving this
			// lib.whiteOut();
			// try {
			// nowPlaying = new Song(newPath, Color.WHITE);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		} else {
			// if the card showing is playlist, not library
			lib.whiteExcept(num, Color.BLUE);
			// nowPlaying = new Song();
			nowPlaying = lib.getSong(num);

		}
	}

	private void play() {

		// for just when open the window
		// it automatically choose the first song assuming the library is not empty
		if (lib.getLength() > 0 && num == 0 && !start) {
			file = new File(lib.getSong(num).getPath());
			start = true;
			System.out.println("loading 1st song " + lib.getSong(num).getPath());
		}
		// for now play will work with main library

		Thread t = new Thread(() -> {
			try {
				// while(autoPlay) {
				// for double click
				if (bis != null && fis != null) {
					bis.close();
					fis.close();
				}
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				player = new AdvancedPlayer(bis);
				// For Play/Stop
				if (pauseLocation <= 0) {
					length = bis.available();
					System.out.println(length);
				} else { // For Pause
					System.out.println(length);
					// fis.skip(length - pauseLocation);
					if (pauseLocation != 1)
						bis.skip(length - pauseLocation + (int) 0.01 * (length - pauseLocation));

				}
				// might have to fix this later for open...
				// have to move cause repeat
				String newPath = file.getPath();
				num = lib.search(newPath);

				if (num == -1) {
					lib.whiteOut();
					nowPlaying = new Song(newPath, Color.WHITE);
				} else {
					// if the card showing is playlist, not library
					lib.whiteExcept(num, Color.BLUE);
					nowPlaying = new Song();
					nowPlaying = lib.getSong(num);
					// System.out.println("titt: "+nowPlaying.title);

				}
				c.start();

				// title.setFont(Font.ITALIC);
				title.setText(nowPlaying.title);
				// System.out.println("titt: "+title.getText());
				pauseLocation = 1;
				player.play(); // put last
				// for autoplay
				// stop();
				// if(num + 1 >= lib.getLength()) {
				// break;
				// }
				// num++;
				// c.reset();
				// file = new File(lib.getSong(num).getPath());
				// } //end of while

			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
	}

	public String setChooser(JButton but) {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("/Users/danhho/Dropbox/PC/Music/"));
		fc.setDialogTitle(but.getText() + " a mp3 file,,,");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showOpenDialog(but) == JFileChooser.APPROVE_OPTION) {

		}
		return fc.getSelectedFile().getAbsolutePath();
	}

	// ====================================ALL THE CLASSES
	// HERE=========================

	public class MyTree extends JPanel {
		private String directory;
		private ArrayList<Playlist> pl;
		private DefaultMutableTreeNode root;

		private JTree tree;

		MyTree() throws IOException {
			this.setBackground(Color.white);
			directory = "/Users/danhho/Dropbox/CS3B/MusicPlayer/playlist";
			pl = new ArrayList<Playlist>();
			buildTree();
			wire();
		}

		private ArrayList<File> listFiles(File folder) {
			ArrayList<File> fileList = new ArrayList<File>();
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile() && file.getPath().endsWith(".txt")) {
					fileList.add(file);
				}
			}
			return fileList;
		}

		private void buildTree() throws IOException {
			File folder = new File(directory);
			ArrayList<File> fileList = listFiles(folder);
			root = new DefaultMutableTreeNode("Root");

			for (File f : fileList) {
				Scanner in = new Scanner(f);
				// name of playlist
				String name = f.getName();
				name = name.substring(0, name.length() - 4);
				DefaultMutableTreeNode root1 = new DefaultMutableTreeNode(name);
				pl.add(new Playlist(name, root1));
				while (in.hasNextLine()) {
					String temp = in.nextLine().trim();
					Song song = new Song(temp, Color.WHITE);
					DefaultMutableTreeNode root2 = new DefaultMutableTreeNode(song.getTitle());
					root1.add(new DefaultMutableTreeNode(root2));
				}
				root.add(root1);
			}

			tree = new JTree(root);
			JScrollPane pane = new JScrollPane(tree);
			pane.setPreferredSize(new Dimension(200, 650));
			// pane.setBounds(0,0 , 200, 650);
			add(pane);
			tree.setRootVisible(false);

		}

		private void wire() {
			tree.addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					JTree tree = (JTree) e.getSource();
					// TODO Auto-generated method stub
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node.isLeaf()) {
						// System.out.println(node.toString());
						// store all path in an array
						// Root|playlist1|IF YOU
						// path[2] = IF YOU
						// Could do a tree search to get the index from the name
						TreeNode[] path = node.getPath();

						c.reset();
						// if(mp != null && mp.getStatus() == Status.PLAYING) {
						// mp.stop();
						// }
						// stop();
						file = lib.Search(path[2].toString());
						start = true; // to prevent playing from the start
						// need to change now Playing
						// play();
						playFile();

					}
					// TreePath path = e.getPath();

					// int pathCount = path.getPathCount();

				}

			});

		}

		private int search(String name) {
			for (int i = 0; i < pl.size(); i++) {
				if (pl.get(i).getName().equals(name)) {
					return i;
				}
			}
			return -1;
		}

		public void addPlaylist(String title) throws FileNotFoundException {
			DefaultMutableTreeNode newList = new DefaultMutableTreeNode(title);
			pl.add(new Playlist(title, newList));
			// how to write a txt file in specific out folder
			PrintWriter out = new PrintWriter(directory + "/" + title + ".txt");
			root.add(newList);
			tree.updateUI();
			out.close();
		}

		public void addSong(Song song, String playlist) throws IOException {
			// make the path default folder later
			String src = directory + "/" + playlist + ".txt";
			try (FileWriter fw = new FileWriter(src, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				// write into playlist txt file
				out.println(song.getPath());
				// maybe wrong here
				int index = search(playlist);
				// System.out.println("index: "+index );
				pl.get(index).getNode().add(new DefaultMutableTreeNode(song.getTitle()));
				// need to store the first order of root
				tree.updateUI();
			}
		}

		public ArrayList<Playlist> getPlaylist() {
			return pl;
		}

		public JTree getTree() {
			return tree;
		}

	}

	public class Playlist {
		private String name;
		private DefaultMutableTreeNode node;

		public Playlist(String name, DefaultMutableTreeNode node) {
			this.name = name;
			this.node = node;
		}

		public String getName() {
			return name;
		}

		public DefaultMutableTreeNode getNode() {
			return node;
		}
	}

	public class Counter {
		private Timer t;
		private int sec = 0;
		private int min = 0;
		private String songTime;
		private int songLen = 0;
		Duration duration;

		public class TimerListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!reachEnd()) {

					// sec++;
					// if (sec > 59) {
					// min++;
					// sec = 0;
					// }
					int t = (int) mp.getCurrentTime().toSeconds();
					System.out.println(t);
					min = t / 60;
					sec = t % 60;
					// System.out.println("get vol: "+ mp.getVolume());

					if (notDrag) {
						System.out.println("not drag: "+ (min * 60 + sec) * TIMEVALUE / songLen);
						
						timeSlider.setValue((min * 60 + sec) * TIMEVALUE / songLen);

						// System.out.println("mouse is not on the bar, automove the slide");
					}
					// reader.set(lyric);

					setTimeText(min, sec);

				} else {
					reset();
				}
				// time.setText(m + ":" + s + " / " + String.format("%02d", songLen / 60) + ":"
				// + String.format("%02d", songLen % 60));
				// mp.currentTimeProperty().addListener((Observable ov) -> {
				// updateValues();
				// setTimeText(min, sec);

				// });

				// mp.setOnReady(() -> {
				// duration = mp.getMedia().getDuration();
				// setTimeText(min, sec);

				// updateValues();
				// });
			}

		};

		public Counter() {

			ActionListener lis = new TimerListener();
			t = new Timer(700, lis);
		}

		public void start() {
			if (nowPlaying != null) {
				songLen = nowPlaying.getLength();
				int t = (int) mp.getCurrentTime().toSeconds();
				min = t / 60;
				sec = t % 60;
				// songLen = (int) mp.getMedia().getDuration().toSeconds();
				songTime = String.format("%02d", songLen / 60) + ":" + String.format("%02d", songLen % 60);
			}
			t.start();
		}

		public void stop() {
			t.stop();

		}

		public void reset() {
			t.stop();
			setTimeText(0, 0);
			timeSlider.setValue(0);

		}

		public void setLen(int len) {
			songLen = len;
		}

		private void setTimeText(int minute, int second) {
			min = minute;
			sec = second;
			String m = String.format("%02d", min);
			String s = String.format("%02d", sec);
			time.setText(m + ":" + s + " / " + songTime);

		}

		public boolean reachEnd() {
			// System.out.println(min + ":" + sec + "/" + songLen);
			if (songLen > 0)
				return min * 60 + sec == songLen - 1;
			else
				return false;
			// return mp.getStatus() == Status.DISPOSED;

		}
	}

	public class Library extends JPanel {
		private ArrayList<Song> songList;
		private String sourcePath;
		private Color panelColor;
		private Color textColor = Color.white;
		private int numOfSong = 0;

		// this is for library
		public Library(String path, Color color) throws IOException {
			sourcePath = path;
			buildLib();
			panelColor = color;
			this.setBackground(panelColor);

		}

		// add when build lib
		private JLabel addSong(String path) throws IOException {
			// song is a jlabel
			Song song = new Song(path, textColor);
			song.setNum(numOfSong);
			numOfSong++;
			songList.add(song);
			this.add(song);
			song.addMouseListener(mlis);
			return song;
		}

		// add song from outside "save"
		public void add(String path) throws IOException {
			this.add(addSong(path));
			this.revalidate();
		}

		private void buildLib() throws IOException {
			songList = new ArrayList<Song>();
			File f = new File(sourcePath);
			Scanner in = new Scanner(f);
			while (in.hasNextLine()) {
				String temp = in.nextLine().trim();
				System.out.println("library printing: " + temp);
				this.add(addSong(temp));

			}
			in.close();
		}

		public int search(String target) {
			for (int i = 0; i < songList.size(); i++) {
				if (target.toLowerCase().equals(songList.get(i).path.toLowerCase())) {
					return i;
				}
			}
			return -1;
		}

		public File Search(String title) {
			for (int i = 0; i < songList.size(); i++) {
				Song s = songList.get(i);
				if (title.equals(s.title)) {
					// System.out.println("order in lib: " +i);
					return new File(s.getPath());
				}
			}
			return null;
		}

		public int getLength() {
			return songList.size();
		}

		public Song getSong(int i) {
			return songList.get(i);
		}

		public void whiteOut() {
			for (JLabel temp : songList) {
				temp.setForeground(textColor);
			}
		}

		public void whiteExcept(int num, Color color) {
			for (JLabel temp : songList) {
				temp.setForeground(textColor);
			}
			songList.get(num).setForeground(color);
			this.revalidate();
		}

		public ArrayList<Song> getSongList() {
			return songList;
		}

	}

	public class Song extends JLabel {
		private Color color;
		private String title;
		private String artist;
		private String album;
		private String year;
		private String path = null;
		private int addedNum = 0;
		private int duration; // duration in second

		public Song() {

		}

		public Song(String path, Color color) throws IOException {
			this.path = path;
			File f = new File(path);
			set(f);
			this.color = color;
			String t = title.trim();
			if (t != null && t.length() > 23) {
				// may set to different title if expand the panel
				System.out.println("Inside song ctor: " + t + " " + t.length());
				t = t.substring(0, 23) + "...";
			}
			this.setText(t);
			this.setForeground(color);
		}

		void set(File file) {
			try {
				BufferedInputStream songReader = new BufferedInputStream(new FileInputStream(file));
				// read duration here
				setLength(file);

				int size = (int) file.length();
				songReader.skip(size - 128);
				byte[] last128 = new byte[128];
				songReader.read(last128);
				String id3 = new String(last128);
				String tag = id3.substring(0, 3);
				if (tag.equals("TAG")) {
					title = id3.substring(3, 32);
					System.out.println("title: " + title);
					artist = id3.substring(33, 62);
					album = id3.substring(63, 91);
					year = id3.substring(93, 97);
				} else {
					System.out.println(" does not contain" + " ID3 information.");
					title = file.getName();
				}

				songReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void setLength(File file) throws IOException {

			try {
				MP3File audioFile = (MP3File) AudioFileIO.read(file);
				duration = audioFile.getAudioHeader().getTrackLength();
				System.out.print("time in milliseconds =  " + duration);
			} catch (CannotReadException e) {
				e.printStackTrace();
			} catch (TagException e) {
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				e.printStackTrace();
			}
		}

		public int getLength() {
			return duration;
		}

		public void setNum(int num) {
			addedNum = num;
		}

		public int getNum() {
			return addedNum;
		}

		public String getTitle() {
			return title;
		}

		public String getPath() {
			return path;
		}

	}

	public class SrtReader {
		private BufferedReader reader;
		private ParseLyric parser;

		public SrtReader(String src) {
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
				parser = new ParseLyric();
				String str = reader.readLine();
				str = reader.readLine();
				String[] strArr = str.split(" ");
				String[] from = strArr[0].split(":");
				String[] to = strArr[2].split(":");
				String result = "";
				// skip enter
				while (!str.equals("")) {
					result += " " + str;
					str = reader.readLine();
				}
				parser.add(calTime(from), calTime(to), result);

				while (reader.ready()) {
					str = reader.readLine();

					try {
						//to read the first integer
						int i = Integer.parseInt(str);
						// System.out.println(i);
						str = reader.readLine();
						strArr = str.split(" ");
						from = strArr[0].split(":");
						to = strArr[2].split(":");
						// System.out.println("from: "+ calTime(from));
						str = reader.readLine();
						result = "";
						// skip enter
						while (!str.equals("")) {
							result += " " + str;
							str = reader.readLine();
						}
						// System.out.println(result);

						parser.add(calTime(from), calTime(to), result);
						// printAll(strArr);
						// printAll(time);

					} catch (NumberFormatException e) {
						// System.out.println("can't parse: " +str);
					}

				}
				System.out.println(parser.getMap());

				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void set(JLabel lyric) {
			String l = parser.search(mp.getCurrentTime().toSeconds());
			if (l != null) {
				lyric.setText(l);
			}
		}

		public boolean within(double from, double to) {
			// if(mp.getCurrentTime().toSeconds())
			double time = mp.getCurrentTime().toSeconds();
			return time >= from && time <= to;
		}

		public double calTime(String[] time) {
			double result = 0;
			// for(String t:time) {
			// result += Double.parseDouble(t);
			// }
			String[] sec = time[2].split(",");

			result = Double.parseDouble(time[0]) * 60;
			result = (result + Double.parseDouble(time[1])) * 60;
			result = result + Double.parseDouble(sec[0]) + Double.parseDouble(sec[1]) / 1000;
			return result;
		}
	}

	public static void main(String[] args) throws JavaLayerException, IOException {
		// String i = "0";
		// System.out.println("parse "+Integer.parseInt(i));
		JFrame f = new JFrame();
		f.setTitle("Danh's Music Player");
		f.setPreferredSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		MusicPlayer mp = new MusicPlayer();
		f.add(mp);
		f.pack(); // need this to be visible
		f.setVisible(true);
	}

	static public void printAll(String[] arr) {
		for (String temp : arr) {
			System.out.println(temp);
		}
	}

}
