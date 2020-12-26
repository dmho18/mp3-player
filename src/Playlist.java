import javax.swing.tree.DefaultMutableTreeNode;

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
