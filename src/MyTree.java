import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class MyTree extends JPanel {
	private String directory;
	private ArrayList<Playlist> pl;
	private DefaultMutableTreeNode root;
	private MusicPlayer mplayer;
	private JTree tree;

	MyTree(MusicPlayer mplayer) throws IOException {
		this.setBackground(Color.white);
		directory = System.getProperty("user.home") + "/Music/Playlist/";
		pl = new ArrayList<Playlist>();
		this.mplayer = mplayer;
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
			name = name.substring(0, name.length() - 4); // minus .mp3
			DefaultMutableTreeNode root1 = new DefaultMutableTreeNode(name);
			pl.add(new Playlist(name, root1));
			while (in.hasNextLine()) {
				String temp = in.nextLine().trim();
				Song song = new Song(temp, Color.WHITE);
				DefaultMutableTreeNode root2 = new DefaultMutableTreeNode(song.getTitle());
				root1.add(new DefaultMutableTreeNode(root2));
			}
			root.add(root1);
			in.close();
		}

		tree = new JTree(root);
		JScrollPane pane = new JScrollPane(tree);
		pane.setPreferredSize(new Dimension(750, 440));
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

					TreeNode[] path = node.getPath();
					mplayer.c.reset();
					if (path.length > 1) { // tree only has up to 2 levels
						mplayer.file = mplayer.table.Search(path[2].toString());
					}
					// file not found;
					if (mplayer.file == null)
						return;
					String newPath = mplayer.file.getPath();
					mplayer.num = mplayer.table.search(newPath);
					if (mplayer.num != -1) {
						mplayer.table.table.setColumnSelectionInterval(0, 0);
						mplayer.table.table.setRowSelectionInterval(mplayer.num, mplayer.num);
					}
				}

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
