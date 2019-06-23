package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Data;

@Data
public class YamlValue implements YamlElement
{
	private String value;
	private String comment = "", preComment = "";
	private Character quoteChar = null;

	public YamlValue(final @NotNull String data)
	{
		value = data;
	}

	public YamlValue(final @NotNull String data, final  @Nullable String comment)
	{
		this(data);
		if(comment != null) this.comment = comment;
	}

	public YamlValue(final @NotNull String data, final @Nullable String comment, final @Nullable Character quoteChar)
	{
		this(data, comment);
		this.quoteChar = quoteChar;
	}
}