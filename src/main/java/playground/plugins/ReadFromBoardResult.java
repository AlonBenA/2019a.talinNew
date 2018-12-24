package playground.plugins;

import java.util.List;

public class ReadFromBoardResult {
	private List<String> results;
	private String activity_id;

	public ReadFromBoardResult() {
	}

	public ReadFromBoardResult(List<String> list, String activity_id) {
		this.results = list;
		this.activity_id = activity_id;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}
	
	public String getActivity_id() {
		return activity_id;
	}
	
	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

}
