package core.config;

import argo.jdom.JsonRootNode;

public interface IJsonable {
	public JsonRootNode jsonize();
}