package at.pcgamingfreaks.yaml;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class that helps reading yaml files from a string
 */
class YamlReader implements AutoCloseable
{
	@Language("RegExp") private static final String QUOTE_PATTERN = "[\"'](?:(?<=\")[^\"\\\\]*(?s:\\\\.[^\"\\\\]*)*\"|(?<=')[^']*(?s:''[^']*)*')";
	@Language("RegExp") private static final String COMMENT_PATTERN = "(?<comment>\\s*#.*)?";
	@Language("RegExp") private static final String COMMENT_PATTERN_INLINE = "(?<comment>\\s+#.*)?";
	private static final Pattern KEY_PATTERN = Pattern.compile("^(?<key>" + QUOTE_PATTERN + "|[^\\s:.'\"]+(\\.[^\\s:.'\"]+)*):");
	private static final Pattern QUOTED_VALUE_PATTERN = Pattern.compile("^(?<value>" + QUOTE_PATTERN + ")" + COMMENT_PATTERN_INLINE + "$");
	private static final Pattern VALUE_PATTERN = Pattern.compile("^(?<value>(.*?))" + COMMENT_PATTERN_INLINE + "$");

	private final YamlNode root;
	private final String[] lines;
	private final Stack<Integer> indentations = new Stack<>();
	private final Stack<YamlNode> nodes = new Stack<>();
	private StringBuilder commentBuilder = new StringBuilder();
	private YamlNode lastNode;

	private int tabIndentationSize = 4, lineNr = 0;
	private boolean tabIndentationSizeSet = false;
	@Getter private @NotNull String footerComment = "";

	YamlReader(final @NotNull String dataString)
	{
		lines = dataString.split("\r?\n");
		lastNode = root = new YamlNode("");
		indentations.push(0);
		nodes.push(root);
	}

	YamlReader(final @NotNull String dataString, final int tabSize)
	{
		this(dataString);
		tabIndentationSize = tabSize;
		tabIndentationSizeSet = true;
	}

	YamlIsMultiLineException mlException = new YamlIsMultiLineException();

	@NotNull YamlNode process() throws YamlInvalidContentException
	{
		String multiline = null;
		int mlStartLineNr = -1;
		for(String line : lines)
		{
			lineNr++;
			//region handle multiline values
			if(multiline != null)
			{
				String trimmedLine = line.trim();
				trimmedLine = (trimmedLine.length() == 0) ? "\n" : " " + trimmedLine;
				line = multiline + trimmedLine;
				multiline = null;
			}
			if(line.matches("[^\\\\]*(\\\\\\\\)*\\\\\\s*$")) // line ends is escaped
			{
				multiline = line.replaceAll("\\\\\\s*$", "");
				if(mlStartLineNr == -1) mlStartLineNr = lineNr;
				continue;
			}
			//endregion
			try
			{
				processLine(line);
			}
			catch(YamlIsMultiLineException ignored) // Allows to process stuff that might cause a multiline error to be processed externally.
			{
				if(mlStartLineNr == -1) mlStartLineNr = lineNr;
				multiline = line;
			}
		}
		if(multiline != null)
			throw new YamlInvalidContentException("Unexpected end of file! Quoted string value started (" + mlStartLineNr + "), but has no end!");
		footerComment = commentBuilder.toString();
		return root;
	}

	private void processLine(@NotNull String line) throws YamlInvalidContentException, YamlIsMultiLineException
	{
		String trimmedLine = line.trim();
		if(line.matches("^\\s*#.*") || line.matches("^\\s*$") || trimmedLine.length() == 0) // if line only contains a comment or is empty
		{
			commentBuilder.append(line);
			commentBuilder.append('\n');
			return;
		}
		int indentation = spacesTillFirstChar(line);
		while(indentation < indentations.peek())
		{
			indentations.pop();
			nodes.pop();
		}
		if(indentation > indentations.peek())
		{
			indentations.push(indentation);
			nodes.push(lastNode);
		}
		if(trimmedLine.startsWith("-"))
		{
			lastNode.addElement(yamlValueBuilder(trimmedLine.substring(1).trim()));
			lastNode.setList(true);
		}
		else
		{
			processNode(line, trimmedLine);
		}
	}

	private void processNode(@NotNull String line, @NotNull String trimmedLine) throws YamlInvalidContentException, YamlIsMultiLineException
	{
		//TODO handle multiline strings that are not escaped correctly
		Matcher matcher = KEY_PATTERN.matcher(trimmedLine);
		if(!matcher.find()) throw new YamlInvalidContentException("The YAML content is invalid, line: " + lineNr);
		String key = matcher.group("key");
		Character quoteChar = null;
		char qChar = key.charAt(0);
		if(qChar == '\'' || qChar == '"')
		{
			quoteChar = qChar;
			key = key.substring(1, key.length() - 1);
		}
		lastNode = new YamlNode(key, null, commentBuilder.toString(), quoteChar);
		nodes.peek().addElement(lastNode);
		commentBuilder = new StringBuilder();
		String data = matcher.replaceFirst("").trim();
		if(data.length() > 0)
		{
			if(data.startsWith("["))
			{
				lastNode.setArray(true);
				readArray(data);
			}
			else
			{
				lastNode.addElement(yamlValueBuilder(data));
			}
		}
	}

	private void readArray(@NotNull String data) throws YamlIsMultiLineException, YamlInvalidContentException
	{
		if(!data.endsWith("]")) throw mlException; //TODO ] at end of multiline string line, comment after array
		String[] values = data.substring(1, data.length() - 1).split(",((?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$))"); //TODO escaped " and ' in "
		for(String value : values)
		{
			if(value.equals("")) continue;
			lastNode.addElement(yamlValueBuilder(value.trim()));
		}
	}

	private @NotNull YamlValue yamlValueBuilder(@NotNull String data) throws YamlIsMultiLineException, YamlInvalidContentException
	{
		String comment = "";
		Character qChar = null;
		if(data.length() > 1) // No need to waste cpu time if the data string is only one char long :D
		{
			char char1 = data.charAt(0);
			if(char1 == '"' || char1 == '\'')
			{
				qChar = char1;
				Matcher matcher = QUOTED_VALUE_PATTERN.matcher(data);
				if(!matcher.matches()) throw mlException;
				comment = matcher.group("comment");
				data = matcher.group("value");
				data = data.substring(1, data.length() - 1);
				switch(char1)
				{
					case '\'': data = data.replaceAll("''", "'"); break;
					case '"': data = data.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\"); break;
				}
			}
			else
			{
				Matcher matcher = VALUE_PATTERN.matcher(data);
				if(!matcher.matches()) throw new YamlInvalidContentException("Invalid value: " + data);
				data = matcher.group("value");
				comment = matcher.group("comment");
			}
		}
		YamlValue value = new YamlValue(data, comment, qChar);
		if(commentBuilder.length() > 0)
		{
			value.setPreComment(commentBuilder.toString());
			commentBuilder = new StringBuilder();
		}
		return value;
	}

	int getTabSize()
	{
		return tabIndentationSize;
	}

	private int spacesTillFirstChar(String str)
	{
		int firstCharacter = 0;
		for (char character : str.toCharArray())
		{
			if (character == ' ')  firstCharacter++;
			else if(character == '\t') firstCharacter += tabIndentationSize;
			else
			{
				if(!tabIndentationSizeSet && firstCharacter != 0)
				{
					tabIndentationSizeSet = true;
					tabIndentationSize = firstCharacter;
				}
				break;
			}
		}
		return firstCharacter;
	}

	@Override
	public void close()
	{
		indentations.clear();
		nodes.clear();
	}

	private static class YamlIsMultiLineException extends Exception {}
}