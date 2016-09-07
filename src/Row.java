import java.util.List;

import org.eclipse.jgit.api.Git;

import javafx.beans.property.BooleanProperty;

public class Row {

	private BooleanProperty enabled;

	private String repositories;
	private String current_version;
	private String latest_version;
	private String last_pulled;
	private List<String> branches;
	private String hash;
	private String description;
	private Git g;

	public Row(BooleanProperty _enabled, String _repositories, String _current_version, String _latest_version,
			String _last_pulled, List<String> branches, String _hash, String _description, Git g) {
		this.enabled = _enabled;
		this.repositories = _repositories;
		this.current_version = _current_version;
		this.latest_version = _latest_version;
		this.last_pulled = _last_pulled;
		this.branches = branches;
		this.hash = _hash;
		this.description = _description;
		this.g = g;
	}

	public Git getGit() {
		return g;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash.toString();
	}

	public BooleanProperty enabledProperty() {
		return enabled;
	}

	public boolean getEnabled() {
		return this.enabled.get();
	}

	public void setEnabled(boolean value) {
		this.enabled.set(value);
	}

	public String getRepositories() {
		return repositories;
	}

	public void setRepositories(String repositories) {
		this.repositories = repositories.toString();
	}

	public String getCurrent_version() {
		return current_version;
	}

	public void setCurrent_version(String current_version) {
		this.current_version = current_version.toString();
	}

	public String getLatest_version() {
		return latest_version;
	}

	public void setLatest_version(String latest_version) {
		this.latest_version = latest_version.toString();
	}

	public String getLast_pulled() {
		return last_pulled;
	}

	public void setLast_pulled(String last_pulled) {
		this.last_pulled = last_pulled.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.toString();
	}
}