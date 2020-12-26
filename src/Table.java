import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class Table extends JPanel {
	private ArrayList<Song> songList;
	private String sourcePath;
	private Color textColor = Color.white;
	private int numOfSong = 0;
	private JScrollPane pane;
	private MusicPlayer mplayer;
	protected MyTable table;
	// private tableModel model;
	private DefaultTableModel model;

	public static final String[] columnNames = { "Title", "Time", "Artist", "Album", "" };

	class ButtonEditor extends DefaultCellEditor {
		protected JButton but;
		protected ImageIcon icon;

		public ButtonEditor(JTextField textField) {
			super(textField);
			but = new JButton();
			but.setOpaque(true);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			icon = (ImageIcon) value;
			but.setIcon(icon);
			return but;
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// TODO Auto-generated method stub
			Icon icon = (Icon) value;
			setIcon(icon);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorderPainted(false);
			setBounds(0, 0, icon.getIconWidth(), icon.getIconWidth());
			return this;
		}

	}

	public class MyTable extends JTable {
		private boolean highlight = false;
		private int hrow;

		public void setHighlight(int row) {
			highlight = true;
			hrow = row;
		}

		public void highlightOn(boolean hl) {
			highlight = hl;
		}

		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Component c = super.prepareRenderer(renderer, row, column);
			if (isRowSelected(row) || hrow == row && highlight) {
				c.setBackground(Color.GREEN);
				c.setForeground(Color.WHITE);
			} else {
				c.setBackground(getBackground());
				c.setForeground(getForeground());
			}
			return c;
		}
	}

	// this is for library
	public Table(String path, MusicPlayer mplayer) throws IOException {
		// fix this
		songList = new ArrayList<Song>();
		model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		model.setColumnIdentifiers(columnNames);
		table = new MyTable();
		table.setModel(model);
		table.setFont(new Font("Seirf", Font.PLAIN, 16));
		table.setRowHeight(35);
		table.setRowSelectionAllowed(true);
		this.mplayer = mplayer;
		sourcePath = path;
		buildTable();
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(300);
		tcm.getColumn(1).setPreferredWidth(5);
		tcm.getColumn(4).setPreferredWidth(1);
		tcm.getColumn(4).setCellRenderer(new ButtonRenderer());
		tcm.getColumn(4).setCellEditor(new ButtonEditor(new JTextField()));

		wire();
		pane = new JScrollPane(table);
		this.setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
	}

	// add when build lib
	private Object[] createRow(Song song) throws IOException {
		int len = song.getLength();
		String m = String.format("%02d", len / 60);
		String s = String.format("%02d", len % 60);
		Image img = ImageIO.read(new File("if_icon-26-trash-can_314863.png"));
		img = img.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(img);
		return new Object[] { song.getTitle(), m + ":" + s, song.getArtist(), song.getAlbum(), icon };
	}

	private Song addSong(String path) throws IOException {
		Song song = new Song(path, textColor);
		song.setNum(numOfSong);
		songList.add(song);
		model.addRow(createRow(song));
		// Order Added
		numOfSong++;

		return song;
	}

	private void wire() {
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isRightMouseButton(e)) {
					int r = table.rowAtPoint(e.getPoint());

					if (table.columnAtPoint(e.getPoint()) == 4) {
						model.removeRow(r);
						File afile = new File(songList.get(r).getPath());
						File newFile = new File(System.getProperty("user.home") + "/Music/" + afile.getName());
						afile.renameTo(newFile);
						
						songList.remove(r);
						if(mplayer.lyric != null)
							mplayer.lyric.setText("");
						// playing vs selected
						if (mplayer.mp == null)
							return;
						if (mplayer.num != r && mplayer.nowPlaying != null) {
							mplayer.num = search(mplayer.nowPlaying.getPath());
							table.setHighlight(mplayer.num);
							return;
						}

						mplayer.mp.stop();
						mplayer.mp = null;
						mplayer.title.setText("");
						mplayer.time.setText("");
						mplayer.num = 0;
						mplayer.c.reset();
						table.highlightOn(false);
						mplayer.setButton(mplayer.play, 50, mplayer.play.getX(), mplayer.play.getY(), "play.png");
						mplayer.start = false;
						return;
					}
					// prevent out of bound selection
					if (r >= 0 && r < table.getRowCount()) {
						// after remove, update highlight
						table.highlightOn(false);
						table.repaint();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {
					int r = table.rowAtPoint(e.getPoint());
					Song song = songList.get(r);
					doPop(e, song);
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			private void doPop(java.awt.event.MouseEvent e, Song song) {
				popUp menu = new popUp(song, mplayer.playlist);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}

		});
		ListSelectionModel selected = table.getSelectionModel();
		selected.addListSelectionListener((ListSelectionListener) new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int c = table.getSelectedColumn();

				if (c == 4) {
					return;
				}
				int r = table.getSelectedRow();
				if (r >= 0 && r < table.getRowCount()) {
					table.highlightOn(false);
					mplayer.nowPlaying = songList.get(r);
					mplayer.num = r;
					Song temp = mplayer.nowPlaying;
					String path = temp.getPath();
					mplayer.file = new File(path);
					mplayer.c.reset();
					mplayer.playFile();
				} else {
					table.clearSelection();
				}

			}
		});
	}

	// add song from outside "save"
	public void add(String path) throws IOException {
		addSong(path);
		this.revalidate();
	}

	private void buildTable() throws IOException {
		File f = new File(sourcePath);
		listFiles(f);

	}

	public JTable getTable() {
		return table;
	}

	public int search(String target) {
		if (target != null) {
			for (int i = 0; i < songList.size(); i++) {
				if (target.toLowerCase().equals(songList.get(i).getPath().toLowerCase())) {
					return i;
				}
			}
		}
		return -1;
	}

	public void listFiles(File folder) {
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			// load only file ends with .mp3
			if (file.isFile() && file.getPath().endsWith(".mp3")) {
				try {
					addSong(file.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void clearAll() {
		table.removeAll();
		model.getDataVector().removeAllElements();
	}

	public void rebuildLib() {
		for (Song song : songList) {
			try {
				model.addRow(createRow(song));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		revalidate();
	}

	public File Search(String title) {
		for (int i = 0; i < songList.size(); i++) {
			Song s = songList.get(i);
			if (title.equals(s.getTitle())) {
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

	public ArrayList<Song> getSongList() {
		return songList;
	}

}
