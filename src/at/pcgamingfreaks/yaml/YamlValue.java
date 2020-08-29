package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Data;

import java.util.regex.Pattern;

@Data
public class YamlValue implements YamlElement
{
	private static final Pattern MUST_BE_QUOTED = Pattern.compile("[^-.\\w]");

	private String value;
	private String comment = "", preComment = "";
	private Character quoteChar = null;

	public YamlValue(final @NotNull String data)
	{
		this(data, null);
	}

	public YamlValue(final @NotNull String data, final  @Nullable String comment)
	{
		this(data, comment, null);
	}

	public YamlValue(final @NotNull String data, final @Nullable String comment, @Nullable Character quoteChar)
	{
		if(comment != null) this.comment = comment;
		this.quoteChar = quoteChar;
		setValue(data);
	}

	public void setValue(final @NotNull String value)
	{
		this.value = value;
		if(quoteChar == null)
		{
			boolean needsQuote = MUST_BE_QUOTED.matcher(value).find();
			if(needsQuote)
			{
				if(value.contains("\"") && !value.contains("'")) quoteChar = '\'';
				else quoteChar = '"';
			}
		}
	}
}