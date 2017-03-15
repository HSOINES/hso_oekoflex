package factories;

import java.util.List;

import oekoflex.OekoflexAgent;

public interface IFactory {
	public List<OekoflexAgent> build(String path);
}
