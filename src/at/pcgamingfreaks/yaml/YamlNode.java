package at.pcgamingfreaks.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.*;

@Data
public class YamlNode implements YamlElement, Cloneable
{
	private String name, comment = "";
	private boolean list = false, array = false;
	private Character quoteChar = null;
	private final List<YamlElement> elements = new LinkedList<>();
	private final Map<String, YamlNode> nodeMap = new HashMap<>();

	@Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE) private int valueCount = 0;

	public YamlNode(String name)
	{
		this.name = name;
		if(name.contains(" ")) quoteChar = '"';
	}

	public YamlNode(@NotNull String name, @Nullable String data) throws YamlInvalidContentException
	{
		this(name);
		if(data != null && data.length() > 0) addElement(new YamlValue(data));
	}

	public YamlNode(@NotNull String name, @Nullable String data, @NotNull String comment) throws YamlInvalidContentException
	{
		this(name, data);
		this.comment = comment;
	}

	public YamlNode(@NotNull String name, @Nullable String data, @NotNull String comment, @Nullable Character quoteChar) throws YamlInvalidContentException
	{
		this(name, data, comment);
		if(this.quoteChar == null || quoteChar != null)	this.quoteChar = quoteChar;
	}

	public YamlNode(final @NotNull YamlNode node)
	{
		this(node.getName());
		setComment(node.getComment());
		setList(node.isList());
		setArray(node.isArray());
		setQuoteChar(node.getQuoteChar());
		elements.addAll(node.getElements());
		nodeMap.putAll(node.getNodeMap());
		valueCount = node.valueCount;
	}

	public void addElement(@NotNull YamlElement element) throws YamlInvalidContentException
	{
		if(element instanceof YamlNode)
		{
			if(valueCount > 1 || list || array) throw new YamlInvalidContentException("It is not possible to add a sub key to a value list!");
			nodeMap.put(((YamlNode) element).getName(), (YamlNode) element);
		}
		else
		{
			if(valueCount == 0 && elements.size() > 0)
			{
				valueCount++;
				elements.add(0, element);
				return;
			}
			if(valueCount != elements.size()) throw new YamlInvalidContentException("It is not possible to add a sub key to a value list!");
			valueCount++;
			if(valueCount > 1) list = true;
		}
		elements.add(element);
	}

	public void addNode(@NotNull YamlNode node) throws YamlInvalidContentException
	{
		if(valueCount > 1 || list || array) throw new YamlInvalidContentException("It is not possible to add a sub key to a value list!");
		nodeMap.put(node.getName(), node);
		float best = -1;
		int i = 0, bestId = -1;
		for(YamlElement element : elements)
		{
			if(element instanceof YamlNode)
			{
				if(((YamlNode) element).getName().startsWith(node.getName())) bestId = i;
				else if(node.getName().startsWith(((YamlNode) element).getName())) bestId = i+1;
				else continue;
				break;
			}
			i++;
		}
		if(bestId >= 0 && bestId < elements.size()) elements.add(bestId, node); else elements.add(node);
	}

	public void removeElement(@NotNull YamlElement element)
	{
		if(element instanceof YamlNode)
		{
			nodeMap.remove(((YamlNode) element).getName());
		}
		else
		{
			valueCount--;
		}
		elements.remove(element);
	}

	public boolean hasValue()
	{
		return valueCount > 0;
	}

	public boolean isValue()
	{
		return valueCount == 1;
	}

	public boolean isValueList()
	{
		return valueCount == elements.size();
	}

	public YamlNode getSubNode(@NotNull String subKey)
	{
		return nodeMap.get(subKey);
	}

	public boolean contains(@NotNull String subKey)
	{
		return nodeMap.containsKey(subKey);
	}

	public @Nullable YamlValue getValue()
	{
		if(isValue())
		{
			return (YamlValue) elements.get(0);
		}
		return null;
	}

	public @NotNull YamlValue getValueE() throws YamlKeyNotFoundException
	{
		if(isValue())
		{
			return (YamlValue) elements.get(0);
		}
		throw new YamlKeyNotFoundException("The given key dose not contain any data");
	}

	public @Nullable List<String> getValuesAsStringList()
	{
		if(isValueList())
		{
			List<String> values = new ArrayList<>(elements.size());
			for(YamlElement element : elements)
			{
				values.add(((YamlValue) element).getValue());
			}
			return values;
		}
		return null;
	}

	public @Nullable List<YamlValue> getValues()
	{
		if(isValueList())
		{
			List<YamlValue> values = new ArrayList<>(elements.size());
			for(YamlElement element : elements)
			{
				values.add((YamlValue) element);
			}
			return values;
		}
		return null;
	}

	public @NotNull List<YamlValue> getValuesE() throws YamlKeyNotFoundException
	{
		if(isValueList())
		{
			List<YamlValue> values = new ArrayList<>(elements.size());
			for(YamlElement element : elements)
			{
				values.add((YamlValue) element);
			}
			return values;
		}
		throw new YamlKeyNotFoundException("The given key dose not contain a list of values");
	}

	private void removeAllValues()
	{
		valueCount = 0;
		elements.removeIf(element -> element instanceof YamlValue);
	}

	private void addNewValue(@NotNull Object value) throws YamlInvalidContentException
	{
		addElement(new YamlValue((value instanceof String) ? (String) value : value.toString()));
	}

	public void set(@Nullable Object value) throws YamlInvalidContentException
	{
		if(value == null)
		{
			list = false;
			removeAllValues();
		}
		else
		{
			if(value.getClass().isArray())
			{
				List<Object> list = new LinkedList<>();
				int len = Array.getLength(value);
				for(int i = 0; i < len; i++)
				{
					list.add(Array.get(value, i));
				}
				value = list;
				array = true;
			}
			if(value instanceof Iterable)
			{
				removeAllValues();
				list = true;
				elements.clear();
				for(Object val : (Iterable<?>) value)
				{
					addElement(new YamlValue((val instanceof String) ? (String) val : val.toString()));
				}
				if(value.getClass().isArray()) array = true;
			}
			else
			{
				if(valueCount > 1) removeAllValues();
				else if(valueCount == 0) addNewValue(value);
				else if(isValue())
				{
					//noinspection ConstantConditions
					getValue().setValue((value instanceof String) ? (String) value : value.toString());
				}
			}
		}
	}
}