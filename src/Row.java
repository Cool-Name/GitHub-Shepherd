public class Row {
	
	private String enabled;
	private String repositories;
	private String current_version;
	private String latest_version;
	private String last_pulled;
	private String description;

	public Row(String _enabled, String _repositories, String _current_version, String _latest_version, String _last_pulled, String _description){
		this.setEnabled(new String(_enabled));
		this.setRepositories(new String(_repositories));
		this.setCurrent_version(new String(_current_version));
		this.setLatest_version(new String(_latest_version));
		this.setLast_pulled(new String(_last_pulled));
		this.setDescription(new String(_description));
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled.toString();
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
