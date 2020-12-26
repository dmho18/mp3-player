
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class MusicPlayer extends JPanel implements ActionListener {
	final static int width = 800;
	final static int height = 750;
	final String LIBPANEL = "Library";
	final String PLAYPANEL = "Playlist";
	final int TIMEVALUE = 1000;
	private Media media;
	protected MediaPlayer mp;
	protected JButton play;
	private JButton stop;
	private JButton save;
	private JButton next;
	private JButton prev;
	private JButton addPlaylist;
	private JButton addLyric;
	protected JLabel title;
	protected JLabel time;
	protected JLabel lyric;
	protected MyTree playlist;
	private JComboBox<String> organize;
	protected boolean start = false;
	protected File file;
	protected Song nowPlaying = null;
	protected Counter c;
	protected JSlider timeSlider;
	private JSlider volume;
	protected int num = 0;
	boolean notDrag = true;
	protected SrtReader reader;
	private JPanel display;
	protected Table table;
	final JFXPanel fxPanel = new JFXPanel();

	public MusicPlayer() throws IOException {
		create();
		wire();
	}

	private void create() throws IOException {
		table = new Table(System.getProperty("user.home") + "/Music/Song", this);
		playlist = new MyTree(this);
		setUI();
	}

	private void setUI() throws IOException {
		this.setLayout(null);
		// control panel - bot
		JPanel controlPanel = new JPanel(null);
		controlPanel.setOpaque(true);
		prev = new JButton();
		setButton(prev, 30, 320, 60, "previous.png");
		play = new JButton();
		setButton(play, 50, 370, 50, "play.png");
		stop = new JButton();
		setButton(stop, 30, 440, 60, "stop.png");
		next = new JButton();
		setButton(next, 30, 500, 60, "next.png");

		title = new JLabel();
		set(title, Color.BLACK, new Dimension(250, 20), 20, 90);
		time = new JLabel();
		set(time, Color.BLACK, new Dimension(100, 20), 680, 90);
		timeSlider = new JSlider(0, TIMEVALUE, 0);
		volume = new JSlider(JSlider.VERTICAL, 0, 100, 100);
		volume.setMajorTickSpacing(10);
		volume.setBounds(750, 20, 20, 70);

		timeSlider.setBounds(10, 10, 780, 10);
		timeSlider.setPaintTrack(true);

		controlPanel.add(volume);
		controlPanel.add(timeSlider);
		controlPanel.add(prev);
		controlPanel.add(play);
		controlPanel.add(stop);
		controlPanel.add(next);
		controlPanel.add(title);
		controlPanel.add(time);
		set(controlPanel, this.getBackground(), new Dimension(800, 150), 0, 590);
		add(controlPanel);

		// top panel
		JPanel top = new JPanel(null);
		save = new JButton();
		setButton(save, 20, 450, 10, "if_Folder_-_Open_256x256-32_172130.png");
		addPlaylist = new JButton();
		setButton(addPlaylist, 20, 500, 10, "if_plus_925914.png");
		addLyric = new JButton();
		setButton(addLyric, 20, 550, 10, "if_Musical_note_2255650.png");

		top.add(save);
		top.add(addPlaylist);
		top.add(addLyric);
		set(top, this.getBackground(), new Dimension(590, 100), 210, 0);
		add(top);

		// display panel
		display = new JPanel();
		set(display, this.getBackground(), new Dimension(800, 60), 0, 540);
		lyric = new JLabel();
		lyric.setFont(new Font("Serif", Font.ITALIC, 18));
		lyric.setForeground(new Color(0, 255, 0));
		lyric.setAlignmentX(Component.CENTER_ALIGNMENT);
		lyric.setAlignmentY(Component.CENTER_ALIGNMENT);
		display.add(lyric);
		add(display);

		// left panel
		JTabbedPane left = new JTabbedPane();
		set(left, Color.LIGHT_GRAY, new Dimension(800, 440), 0, 100);
		left.addTab(LIBPANEL, table);

		left.addTab(PLAYPANEL, playlist);
		add(left);

		// top left
		JPanel func = new JPanel();
		set(func, null, new Dimension(200, 100), 10, 0);
		func.add(new JLabel("Sort: "));
		organize = new JComboBox<String>(new String[] { "Order Added", "Title", "Length", "Artist", "Album" });
		func.add(organize);
		add(func);

	}

	private void setTimeSlider() {

		timeSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				changeState();
				notDrag = true;
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				changeState();
				notDrag = true;
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				notDrag = true;
			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
			}
		});
		timeSlider.addMouseMotionListener((MouseMotionListener) new MouseMotionListener() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				changeState();
			}

			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
			}
		});
	}

	public void setButton(JButton but, int scale, int x, int y, String path) {
		Image img;
		try {
			img = ImageIO.read(new File(path));
			img = img.getScaledInstance(scale, scale, java.awt.Image.SCALE_SMOOTH);
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

	private void set(JComponent jc, Color color, Dimension displaySize, int x, int y) {
		if (jc instanceof JButton || jc instanceof JLabel || jc instanceof JPanel || jc instanceof JTabbedPane) {
			if (jc instanceof JLabel) {
				jc.setForeground(color);
			} else
				jc.setBackground(color);
			jc.setBounds(x, y, displaySize.width, displaySize.height);
		}
	}

	private void wire() {
		c = new Counter(this);

		play.addActionListener(this);
		stop.addActionListener(this);
		save.addActionListener(this);
		next.addActionListener(this);
		prev.addActionListener(this);
		addPlaylist.addActionListener(this);
		addLyric.addActionListener(this);

		setTimeSlider();
		organize.addActionListener(new ActionListener() {
			Sort sort = new Sort(table);

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> combo = (JComboBox<String>) e.getSource();
				String selected = (String) combo.getSelectedItem();
				if (selected.equals("Order Added")) {
					sort.byOrder();
				} else if (selected.equals("Title")) {
					sort.byTitle();
				} else if (selected.equals("Length")) {
					sort.byLen();
				} else if (selected.equals("Artist")) {
					sort.byArtist();
				} else if (selected.equals("Album")) {
					sort.byAlbum();
				}
				table.clearAll();
				table.rebuildLib();
				// update to current num;
				if (nowPlaying != null) {
					num = table.search(nowPlaying.getPath());
					// highlight, cause after rebuild highlight is gone
					table.table.setHighlight(num);
				}
			}

		});
		volume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				mp.setVolume(s.getValue() * 1.0 / 100);
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton but = (JButton) e.getSource(); // casting
		if (but.equals(play)) {

			if (mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING) {
				mp.pause();
				setButton(play, 50, play.getX(), play.getY(), "play.png");

			} else {
				if (!start && table.getLength() > 0 && num == 0) {
					file = new File(table.getSong(num).getPath());
					start = true;
					// reset selection here //col before row
					table.table.setColumnSelectionInterval(0, 0);
					table.table.setRowSelectionInterval(0, 0);
				} else {
					setButton(play, 50, play.getX(), play.getY(), "pause.png");
					if (mp == null)
						return;
					mp.play();
					c.start();

				}
			}
		} else if (but == stop) {
			if (mp != null) {
				c.reset();
				mp.stop();
				lyric.setText("");
				setButton(play, 50, play.getX(), play.getY(), "play.png");
			}

		} else if (but == save) {
			String path = setChooser(save);
			if (path == null)
				return;

			int num = table.search(path);
			// prevent adding duplicate
			if (num == -1)
				try {
					File afile = new File(path);
					File newFile = new File(System.getProperty("user.home") + "/Music/Song/" + afile.getName());
					afile.renameTo(newFile);
					table.add(newFile.getPath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		} else if (but == next) {
			playNext();
		} else if (but == prev) {
			playPrev();
		} else if (but == addPlaylist) {
			JPanel pan = new JPanel(new BorderLayout());

			JLabel l = new JLabel("Playlist Name: ");
			JTextField tf = new JTextField(20);

			JButton b = new JButton("Enter");
			pan.add(l, BorderLayout.NORTH);
			pan.add(tf, BorderLayout.CENTER);
			pan.add(b, BorderLayout.SOUTH);

			JFrame f = new JFrame("Add A Playlist");
			f.setPreferredSize(new Dimension(200, 100));
			f.add(pan);
			f.setLocation(300, 300);
			f.setResizable(false);
			f.pack(); // need this to be visible
			f.setVisible(true);
			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JButton button = (JButton) e.getSource(); // casting
					// TODO Auto-generated method stub
					if (button == b) {
						String name = tf.getText().trim();
						if (!name.isEmpty()) {
							tf.setText("");
						}
						try {
							playlist.addPlaylist(name);
							// in case it won;t update
							playlist.getTree().updateUI();

						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						f.setVisible(false);
					}
				}

			});
		} else if (but == addLyric) {
			String path = setChooser(addLyric);
			if (path != null)
				reader = new SrtReader(path);

		}

	}

	private void playPrev() {
		if (num - 1 < 0) {
			return;
		}
		num--;
		table.table.setColumnSelectionInterval(0, 0);
		table.table.setRowSelectionInterval(num, num);

	}

	private void playNext() {

		if (num + 1 >= table.getLength()) {
			return;
		}
		if (num >= 0) {
			num++;
		}
		table.table.setColumnSelectionInterval(0, 0);
		table.table.setRowSelectionInterval(num, num);

	}

	public void playFile() {

		reader = null;
		title.setText(nowPlaying.getTitle());
		// will reset everything here
		if (mp != null && mp.getStatus() == Status.PLAYING) {
			mp.stop();
		}
		start = true;
		media = new Media(file.toURI().toString());
		mp = new MediaPlayer(media);
		c.start();
		mp.play();
		setButton(play, 50, play.getX(), play.getY(), "pause.png");
	}

	public String setChooser(JButton but) {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Music/Song"));
		fc.setDialogTitle("Choose a file");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showOpenDialog(but) == JFileChooser.APPROVE_OPTION) {

		}
		File f = fc.getSelectedFile();
		if (f != null) {
			return f.getAbsolutePath();
		} else {
			return null;
		}

	}

	public static void main(String[] args) throws IOException {

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

}
