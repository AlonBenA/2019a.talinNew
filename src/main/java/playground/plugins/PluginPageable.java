package playground.plugins;

public class PluginPageable {
	private int size;
	private int page;
	
	public PluginPageable() {
		page = 0;
		size = 10;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	
}
