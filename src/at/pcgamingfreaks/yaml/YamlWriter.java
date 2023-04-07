package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper class to convert a yaml object beck to a string
 */
class YamlWriter implements AutoCloseable
{
	private static final Pattern NEEDS_ESCAPE_PATTERN = Pattern.compile("[\\s:\\\\\"'\\.]");

	private final @NotNull YamlNode root;
	private StringBuilder yamlBuilder;
	private String tab = "    ", footerComment = "", newLineSymbol = "\n";

	YamlWriter(@NotNull YamlNode root)
	{
		this.root = root;
	}

	YamlWriter(@NotNull YamlNode root, int tabSize)
	{
		this(root);
		StringBuilder tabBuilder = new StringBuilder();
		while(--tabSize >= 0)
		{
			tabBuilder.append(" ");
		}
		tab = tabBuilder.toString();
	}

	YamlWriter(@NotNull YamlNode root, int tabSize, @NotNull String footerComment)
	{
		this(root, tabSize);
		this.footerComment = footerComment;
	}

	YamlWriter(@NotNull YamlNode root, int tabSize, @NotNull String footerComment, @NotNull String newLineSymbol)
	{
		this(root, tabSize, footerComment);
		this.newLineSymbol = newLineSymbol;
	}

	@NotNull String process() throws YamlInvalidContentException
	{
		yamlBuilder = new StringBuilder();
		process(root, "");
		yamlBuilder.append(footerComment);
		return yamlBuilder.toString();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isRoot(@Nullable YamlNode node)
	{
		//noinspection ObjectEquality
		return node == root; // This is ok, we need to check if it is the same object as the root object, not if the value is the same
	}

	private void process(@NotNull YamlNode node, @NotNull String indentation) throws YamlInvalidContentException
	{
		String nextIndentation;
		if(!isRoot(node))
		{
			nextIndentation = indentation + tab;
			yamlBuilder.append(node.getComment());
			yamlBuilder.append(indentation);
			yamlBuilder.append(formatNodeName(node, indentation)).append(':');
		}
		else nextIndentation = "";
		if(node.isArray()) writeArray(node, nextIndentation);
		else if(node.isList()) writeList(node, nextIndentation);
		else
		{
			if(!node.hasValue() && !isRoot(node)) yamlBuilder.append(newLineSymbol);
			for(YamlElement child : node.getElements())
			{
				if(child instanceof YamlValue)
				{
					writeValue((YamlValue) child, nextIndentation);
					yamlBuilder.append(newLineSymbol);
				}
				else if(child instanceof YamlNode)
				{
					process((YamlNode) child, nextIndentation);
					yamlBuilder.append(newLineSymbol);
				}
			}
			yamlBuilder.deleteCharAt(yamlBuilder.length() - 1);
		}
	}

	private boolean stringNeedsEscaping(final @NotNull String string)
	{
		return NEEDS_ESCAPE_PATTERN.matcher(string).find();
	}

	private @NotNull String formatNodeName(final @NotNull YamlNode node, final @NotNull String indentation)
	{
		char preferredEscape = '\0';
		if (stringNeedsEscaping(node.getName()))
		{
			if(node.getName().contains("'"))
			{
				return quoteString(node.getName(), '"', indentation);
			}
			else
			{
				return quoteString(node.getName(), '\'', indentation);
			}
		}
		return node.getName();
	}

	private @NotNull String quoteString(@NotNull String val, @Nullable Character quoteChar, @NotNull String indentation)
	{
		if(quoteChar != null)
		{
			switch(quoteChar)
			{
				case '"':
					val = val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
					break;
				case '\'':
					val = val.replace("'", "''");
			}
			val = quoteChar + val + quoteChar;
		}
		if(quoteChar == null || quoteChar == '\'')
		{
			val = val.replace("\n", "\n" + indentation + "\n" + indentation);
		}
		return val;
	}

	private void writeValue(@NotNull YamlValue value, @NotNull String indentation)
	{
		yamlBuilder.append(' ');
		yamlBuilder.append(quoteString(value.getValue(), value.getQuoteChar(), indentation));
		yamlBuilder.append(value.getComment());
	}

	private void writeArray(@NotNull YamlNode node, @NotNull String indentation) throws YamlInvalidContentException
	{
		List<YamlValue> values = node.getValues();
		if(values == null) throw new YamlInvalidContentException("Node is marked as an array, but does not contain an array");
		yamlBuilder.append(" [");
		String prefix = "";
		for(YamlValue value : values)
		{
			yamlBuilder.append(prefix);
			writeValue(value, indentation + tab);
			prefix = ",";
		}
		if(values.size() > 0) yamlBuilder.append(' ');
		yamlBuilder.append(']');
	}

	private void writeList(@NotNull YamlNode node, @NotNull String indentation) throws YamlInvalidContentException
	{
		String prefix = indentation + "-";
		List<YamlValue> values = node.getValues();
		if(values == null) throw new YamlInvalidContentException("Node is marked as a list does not contain a list");
		for(YamlValue value : values)
		{
			yamlBuilder.append(newLineSymbol);
			yamlBuilder.append(value.getPreComment());
			yamlBuilder.append(prefix);
			writeValue(value, indentation + tab);
		}
	}

	@Override
	public void close()
	{
		tab = null;
		footerComment = null;
		yamlBuilder = null;
		newLineSymbol = null;
	}
}