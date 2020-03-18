package br.com.model.editor.model;

import java.io.Serializable;
import java.util.List;

import br.com.model.editor.data.Server;

public class ModelData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<TableModel> models;
	private Renames renames;
	private Server server;

	public ModelData(List<TableModel> models, Renames renames, Server server) {
		this.models = models;
		this.renames = renames;
		this.server = server;
	}

	public List<TableModel> getModels() {
		return models;
	}

	public void setModels(List<TableModel> models) {
		this.models = models;
	}

	public Renames getRenames() {
		return renames;
	}

	public void setRenames(Renames renames) {
		this.renames = renames;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
