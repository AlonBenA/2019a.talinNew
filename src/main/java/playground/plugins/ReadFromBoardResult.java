package playground.plugins;

import java.util.List;

public class ReadFromBoardResult {
	private List<String> results;

	public ReadFromBoardResult() {
	}

	public ReadFromBoardResult(List<String> results) {
		this.results = results;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

}
