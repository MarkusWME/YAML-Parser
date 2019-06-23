package at.pcgamingfreaks.yaml;

public class YamlInvalidContentException extends Exception
{
	YamlInvalidContentException()
	{
		super();
	}

	YamlInvalidContentException(String message)
	{
		super(message);
	}
}