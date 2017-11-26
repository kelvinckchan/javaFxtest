package app.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of models. This is used for saving the list of
 * models to XML.
 * 
 * @author Marco Jakob
 */
@XmlRootElement(name = "models")
public class ModelWrapper {

	private List<Model> models;

	@XmlElement(name = "model")
	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}
}