package at.pcgamingfreaks.yaml;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class YAML implements AutoCloseable, YamlGetter
{
	private static final int BOM_SIZE = 4;

	private final Map<String, YamlNode> nodeMap = new HashMap<>(), valueNodeMap = new HashMap<>();
	private String encoding = "UTF-8", footer;
	private YamlNode root = null;
	private int tabSize = 4;

	//region Constructors
	private YAML() {}

	/**
	 * Constructor to initialize a YAML object and load data from a file
	 * @param file The file that should be loaded to the YAML object
	 * @throws IOException If any file handling failed
	 * @throws YamlInvalidContentException If the YAML content is invalid
	 */
	public YAML(final @NotNull File file) throws IOException, YamlInvalidContentException
	{
		load(file);
	}

	/**
	 * Constructor to initialize a YAML object and load data from a stream
	 * @param stream The stream to load data from
	 * @exception IOException If any handling with the stream failed
	 * @throws YamlInvalidContentException If the YAML content is invalid
	 */
	public YAML(final @NotNull InputStream stream) throws IOException, YamlInvalidContentException
	{
		load(stream);
	}

	/**
	 * Constructor to initialize a YAML object and load data from a string
	 * @param dataString The YAML data that should be processed to the YAML object
	 * @throws YamlInvalidContentException If the YAML content is invalid
	 */
	public YAML(final @NotNull String dataString) throws YamlInvalidContentException
	{
		load(dataString);
	}
	//endregion

	//region Load method
	/**
	 * Function to load data from a file to the YAML object
	 * @param file File object to load data from
	 * @throws IOException If any file handling failed
	 * @throws YamlInvalidContentException If the YAML content is invalid
	 */
	public void load(final @NotNull File file) throws IOException, YamlInvalidContentException
	{
		try (FileInputStream inputStream = new FileInputStream(file))
		{
			load(inputStream);
		}
	}

	/**
	 * Function to load data from a stream to the YAML object
	 * @param stream The stream from where data should be loaded to the YAML object
	 * @throws IOException If any handling with the stream failed
	 * @throws YamlInvalidContentException If the YAML content is invalid
	 */
	public void load(final @NotNull InputStream stream) throws IOException, YamlInvalidContentException
	{
		byte[] bom = new byte[BOM_SIZE];
		encoding = "UTF-8";
		int unread;
		try(PushbackInputStream pushbackInputStream = new PushbackInputStream(stream, BOM_SIZE))
		{
			int count = unread = pushbackInputStream.read(bom, 0, BOM_SIZE);
			if(count >= 3 && bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)
			{
				encoding = "UTF-8";
				unread = count - 3;
			}
			else if(count >= 2 && bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF)
			{
				encoding = "UTF-16BE";
				unread = count - 2;
			}
			else if(count >= 2 && bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE)
			{
				if(count >= 4 && bom[2] == (byte) 0x00 && bom[3] == (byte) 0x00)
				{
					encoding = "UTF-32LE";
					unread = count - 4;
				}
				else
				{
					encoding = "UTF-16LE";
					unread = count - 2;
				}
			}
			else if(count >= 4 && bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00 && bom[2] == (byte) 0xFE && bom[3] == (byte) 0xFF)
			{
				encoding = "UTF-32BE";
				unread = count - 4;
			}
			if(unread > 0)
			{
				pushbackInputStream.unread(bom, (count - unread), unread);
			}
			else if(unread < -1)
			{
				pushbackInputStream.unread(bom, 0, 0);
			}
			try(Scanner scanner = new Scanner(pushbackInputStream, encoding))
			{
				load(scanner.useDelimiter("\\Z").next());
			}
		}
	}

	/**
	 * Function to load data from a given string to the YAML object
	 * @param dataString The data string from which data should be loaded to the object
	 */
	public void load(final @NotNull String dataString) throws YamlInvalidContentException
	{
		try(YamlReader reader = new YamlReader(dataString))
		{
			load(reader.process());
			footer = reader.getFooterComment();
			tabSize = reader.getTabSize();
		}
		catch(YamlInvalidContentException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Function to load data from a given yaml tree to the YAML object
	 * @param root The root node of the yaml object from which data should be loaded
	 */
	public void load(final @NotNull YamlNode root)
	{
		clear();
		this.root = root;
		load("", root);
	}

	/**
	 * Function to load data from a given yaml tree to the YAML object with a new root node
	 * @param root The root node of the yaml object from which data should be loaded
	 */
	public void loadNewRoot(final @NotNull YamlNode root)
	{
		clear();
		this.root = new YamlNode(root);
		this.root.setName("");
		load("", this.root);
	}

	private void load(@NotNull String parentKey, final @NotNull YamlNode node)
	{
		if(parentKey.length() > 0) parentKey += '.';
		parentKey += node.getName();
		nodeMap.put(parentKey, node);
		if(node.hasValue() || node.isArray()) valueNodeMap.put(parentKey, node);
		if(!node.isList())
		{
			for(YamlElement child : node.getElements())
			{
				if(child instanceof YamlNode)
				{
					load(parentKey, (YamlNode) child);
				}
			}
		}
	}
	//endregion

	//region Save method
	/**
	 * Saves the content of the YAML object into a file
	 * @throws FileNotFoundException If the file could not be found
	 */
	public void save(@NotNull File file) throws FileNotFoundException
	{
		try(FileOutputStream stream = new FileOutputStream(file); PrintStream out = new PrintStream(stream, true, encoding))
		{
			out.append(saveAsString());
			out.flush();
		}
		catch(FileNotFoundException e)
		{
			throw e;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Saves the content of the YAML object into a stream
	 * @return The YAML stream that represents the object
	 * @throws IOException If any IO handling fails
	 */
	public @NotNull OutputStream saveAsStream() throws IOException
	{
		OutputStream stream = new ByteArrayOutputStream();
		try(DataOutputStream out = new DataOutputStream(stream))
		{
			out.writeBytes(saveAsString());
			out.flush();
		}
		return stream;
	}

	/**
	 * Saves the content of the YAML object into a string
	 * @return The YAML string that represents the object
	 */
	public @NotNull String saveAsString()
	{
		try(YamlWriter writer = new YamlWriter(root, tabSize, footer))
		{
			return writer.process();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	//endregion

	//region Cleanup methods
	/**
	 * Function to clear all data of the YAML object
	 */
	public void clear()
	{
		root = null;
		nodeMap.clear();
		valueNodeMap.clear();
		footer = "";
	}

	/**
	 * Function that frees all resources of the YAML object
	 */
	public void dispose()
	{
		close();
	}

	/**
	 * Function that frees all resources of the YAML object
	 */
	@Override
	public void close()
	{
		clear();
	}
	//endregion

	//region getter
	/**
	 * Checks if the given key exists in the YAML object
	 * @param key The key that should be checked
	 * @return If the key exists in the object or not
	 */
	@Override
	public boolean isSet(@NotNull String key)
	{
		return valueNodeMap.containsKey(key);
	}

	/**
	 * Function to get the keys of the YAML object
	 * @return The keys of the YAML object in a Set object
	 */
	public @NotNull Set<String> getKeys()
	{
		return new HashSet<>(valueNodeMap.keySet());
	}

	public @NotNull Collection<String> getKeysFiltered(final @Language("RegExp") String filterRegex)
	{
		return getKeys().stream().filter(key -> key.matches(filterRegex)).collect(Collectors.toList());
	}

	/**
	 * Function to get the keys of the YAML object
	 * @param subKeys If set to false the function only returns the high level keys
	 * @return The key set of the YAML object
	 */
	public @NotNull Set<String> getKeys(boolean subKeys)
	{
		if (subKeys)
		{
			return getKeys();
		}
		Set<String> returnedKeys = new HashSet<>();
		for (String key : getKeys())
		{
			if (key.indexOf('.') < 0)
			{
				returnedKeys.add(key);
			}
		}
		return returnedKeys;
	}

	/**
	 * Gets a yaml value element from the YAML object
	 *
	 * @param key The key of the value you want to get
	 * @return The value of the key you searched for
	 */
	@Override
	public @Nullable YamlValue getValue(@NotNull String key)
	{
		YamlNode node = valueNodeMap.get(key);
		if(node != null) return node.getValue();
		return null;
	}

	/**
	 * Gets a list of strings from the YAML object
	 * @param key The key of the list you want to get
	 * @param defaultValue The default value that should be returned if the key could not be found
	 * @return The list of values you searched for
	 */
	@Override
	public @Nullable List<String> getStringList(@NotNull String key, @Nullable List<String> defaultValue)
	{
		YamlNode node = valueNodeMap.get(key);
		if(node != null)
		{
			List<String> list = node.getValuesAsStringList();
			if(list != null) return list;
		}
		return defaultValue;
	}

	/**
	 * Gets a section of the YAML object
	 * @param key Key of the section you want to get
	 * @return The selected section
	 */
	public @NotNull YAML getSection(@NotNull String key) throws YamlKeyNotFoundException
	{
		YamlNode node = nodeMap.get(key);
		if(node == null) throw new YamlKeyNotFoundException("Key " + key + " not found");
		YAML section = new YAML();
		section.loadNewRoot(node);
		return section;
	}

	public boolean isListE(@NotNull String key) throws YamlKeyNotFoundException
	{
		YamlNode node = nodeMap.get(key);
		if(node == null) throw new YamlKeyNotFoundException("Key " + key + " not found");
		return node.isList();
	}

	public boolean isList(@NotNull String key)
	{
		YamlNode node = nodeMap.get(key);
		return node != null && node.isList();
	}
	//endregion

	/**
	 * Sets the value for the given key in the YAML object
	 * @param key The key for which the value should be set
	 * @param value The value that should be assigned
	 */
	public void set(@NotNull String key, @Nullable Object value)
	{
		try
		{
			if(nodeMap.containsKey(key))
			{
				nodeMap.get(key).set(value);
			}
			else
			{
				String[] keys = key.split("\\.");
				YamlNode node = root, nNode;
				StringBuilder keyBuilder = new StringBuilder();
				for(String sKey : keys)
				{
					if(keyBuilder.length() > 0) keyBuilder.append('.');
					keyBuilder.append(sKey);
					nNode = node.getSubNode(sKey);
					if(nNode == null)
					{
						nNode = new YamlNode(sKey);
						node.addNode(nNode);
						nodeMap.put(keyBuilder.toString(), nNode);
					}
					node = nNode;
				}
				node.set(value);
				valueNodeMap.put(keyBuilder.toString(), node);
			}
		}
		catch(YamlInvalidContentException e)
		{
			e.printStackTrace();
		}
	}
}