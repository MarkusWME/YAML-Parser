import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tests
{
	@Test
	public void testList() throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		try(YAML listValueTest = new YAML("- 1\n- 2\n- 3"))
		{
			assertEquals(1, (int) listValueTest.getIntList("").get(0));
			assertEquals(2, (int) listValueTest.getIntList("").get(1));
			assertEquals(3, (int) listValueTest.getIntList("").get(2));
		}
	}

	@Test
	public void testMapping() throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		try(YAML mappingValueTest = new YAML("Test: 1\nTest2: 2\nTest3: 3"))
		{
			assertEquals(1, mappingValueTest.getInt("Test"));
			assertEquals(2, mappingValueTest.getInt("Test2"));
			assertEquals(3, mappingValueTest.getInt("Test3"));
		}
	}

	@Test
	public void testGetKeys() throws YamlInvalidContentException
	{
		HashSet<String> keys = new HashSet<>();
		try(YAML keyTest = new YAML("Line1: text\nLine2: text\nLine3: text\nLine4:\n  - group1\n  -group2"))
		{
			keys.add("Line1");
			keys.add("Line2");
			keys.add("Line3");
			keys.add("Line4");
			assertEquals(keys, keyTest.getKeys(false));
		}
		try(YAML keyTest = new YAML("Line1: text\nLine2: text\nListEmpty: []\nList:\n  - group1\n  -group2\nListInline: [ stuff, moreStuff]"))
		{
			keys = new HashSet<>();
			keys.add("Line1");
			keys.add("Line2");
			keys.add("ListEmpty");
			keys.add("List");
			keys.add("ListInline");
			assertEquals(keys, keyTest.getKeys(false));
		}
	}

	@Test
	public void testGetKeysFiltered() throws YamlInvalidContentException
	{
		try(YAML keyTest = new YAML("Line1: text\nLine2: text\nLine3: text\nLine4:\n  - group1\n  -group2"))
		{
			ArrayList<String> keys = new ArrayList<>(2);
			keys.add("Line1");
			keys.add("Line3");
			assertEquals(keys, keyTest.getKeysFiltered("Line[13]"));
		}
		try(YAML keyTest = new YAML("Line1: text\nLine2: text\nListEmpty: []\nList:\n  - group1\n  -group2\nListInline: [ stuff, moreStuff]"))
		{
			ArrayList<String> keys = new ArrayList<>(3);
			keys.add("ListEmpty");
			keys.add("List");
			keys.add("ListInline");
			assertEquals(keys, keyTest.getKeysFiltered("List.*"));
		}
	}

	@Test
	public void testNamedListWithIndentation() throws YamlKeyNotFoundException, YamlInvalidContentException
    {
		try(YAML listValueTest = new YAML("List:\n  - This\n  - is\n  - a\n  - list"))
		{
			List<String> expected = new ArrayList<>();
			expected.add("This");
			expected.add("is");
			expected.add("a");
			expected.add("list");
			assertEquals(expected, listValueTest.getStringList("List"));
		}
	}

	@Test
	public void testNamedListWithoutIndentation() throws YamlKeyNotFoundException, YamlInvalidContentException
    {
		try(YAML listValueTest = new YAML("List:\n- This\n- is\n- a\n- list"))
		{
			List<String> expected = new ArrayList<>();
			expected.add("This");
			expected.add("is");
			expected.add("a");
			expected.add("list");
			assertEquals(expected, listValueTest.getStringList("List"));
		}
	}

	@Test
	public void testNamedListWithoutIndentationAndMultipleData() throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		List<String> expected = new ArrayList<>(), expected2 = new ArrayList<>(), expected3 = new ArrayList<>();
		expected.add("This");
		expected.add("is");
		expected.add("a");
		expected.add("list");
		expected2.add("This");
		expected2.add("is");
		expected3.add("a");
		expected3.add("list");
		try(YAML listValueTest = new YAML("Document:\n  List:\n  - This\n  - is\n  - a\n  - list"))
		{
			assertEquals(expected, listValueTest.getStringList("Document.List"));
		}
		try(YAML listValueTest = new YAML("Document:\n  List1:\n  - This\n  - is\n  List2:\n  - a\n  - list"))
		{
			assertEquals(expected2, listValueTest.getStringList("Document.List1"));
			assertEquals(expected3, listValueTest.getStringList("Document.List2"));
		}
		try(YAML listValueTest = new YAML("this:\n  Document:\n    List:\n    - This\n    - is\n    - a\n    - list"))
		{
			assertEquals(expected, listValueTest.getStringList("this.Document.List"));
		}
	}

    @Test
	public void testMultiLineTexts() throws YamlInvalidContentException, YamlKeyNotFoundException
    {
		try(YAML multilineTest = new YAML("test: value\nmulti:\n- 'This is a\\\n  test text'\n- \"And this is\\\n   another one\"\ntest2: \"another value\""))
		{
			assertEquals("value", multilineTest.getString("test"));
			assertEquals("another value", multilineTest.getString("test2"));
			assertEquals("This is a test text", multilineTest.getStringList("multi").get(0));
			assertEquals("And this is another one", multilineTest.getStringList("multi").get(1));
		}
	}

	@Test
	public void testMultiLineTextsWithoutEscape() throws YamlInvalidContentException, YamlKeyNotFoundException
	{
		try(YAML multilineTest = new YAML("test: value\nmulti:\n- \"This is a\n   test text\"\n- \"And this is\n   another one\"\ntest2: \"another value\""))
		{
			assertEquals("value", multilineTest.getString("test"));
			assertEquals("another value", multilineTest.getString("test2"));
			assertEquals("This is a test text", multilineTest.getStringList("multi").get(0));
			assertEquals("And this is another one", multilineTest.getStringList("multi").get(1));
		}
	}

	@Test
	public void testEscapedKeys() throws YamlInvalidContentException, YamlKeyNotFoundException
	{
		try(YAML multilineTest = new YAML("'269': value\nmulti:\n- \"This is a\n   test text\"\n- \"And this is\n   another one\"\ntest.2: \"test\""))
		{
			assertEquals("value", multilineTest.getString("269"));
			assertEquals("test", multilineTest.getString("test.2"));
			assertEquals("This is a test text", multilineTest.getStringList("multi").get(0));
			assertEquals("And this is another one", multilineTest.getStringList("multi").get(1));
		}
	}

	@Test
	public void testSet() throws YamlInvalidContentException, YamlKeyNotFoundException
	{
		try(YAML saveTest = new YAML("Language:\n" + "  Lang1: \"text\"\n" + "  Lang2: \"ext\"\n" + "  Lang3: \"xt\"\n" + "  Lang4: \"t\""))
		{
			assertEquals("text", saveTest.getString("Language.Lang1"));
			saveTest.set("Language.Lang1", "New language value");
			assertEquals("New language value", saveTest.getString("Language.Lang1"));
			saveTest.set("Language.Lang1000", "New language key and value");
			assertEquals("New language key and value", saveTest.getString("Language.Lang1000"));
		}
	}

	@Test
	public void testArray() throws YamlKeyNotFoundException, YamlInvalidContentException
	{
		try(YAML yaml = new YAML("Data: [ data1, data2 ]\nEmpty: []"))
		{
			List<String> list = new ArrayList<>();
			assertEquals(list, yaml.getStringList("Empty"));
			list.add("data1");
			list.add("data2");
			assertEquals(list, yaml.getStringList("Data"));
		}
	}

	@Test
	public void testGetSection() throws YamlInvalidContentException, YamlKeyNotFoundException
	{
		try(YAML yaml = new YAML("Data1:\n  Data1_2: true\nData2:\n  Data2_1: true"); YAML section = yaml.getSection("Data2"))
		{
			Set<String> keys = new HashSet<>();
			keys.add("Data2_1");
			assertEquals(keys, section.getKeys());
			assertTrue(section.isSet("Data2_1"));
			assertTrue(section.getBoolean("Data2_1"));
		}
	}

	@Test
	public void testSetStringWithSpecialChars() throws YamlInvalidContentException, YamlKeyNotFoundException
	{
		String yamlText = "Database:\n" + "  User: \"user\"\n" + "  Password: password\n" + "  Host: 'localhost'";
		try(YAML saveTest = new YAML(yamlText))
		{
			assertEquals("user", saveTest.getString("Database.User"));
			assertEquals("password", saveTest.getString("Database.Password"));
			assertEquals("localhost", saveTest.getString("Database.Host"));

			saveTest.set("Database.User", "u\"s#er");
			saveTest.set("Database.Password", "P@SS#WORD");
			saveTest.set("Database.Host", "local'host");
			saveTest.set("Database.Meta", "daf#afd");

			assertEquals("u\"s#er", saveTest.getString("Database.User"));
			assertEquals("P@SS#WORD", saveTest.getString("Database.Password"));
			assertEquals("local'host", saveTest.getString("Database.Host"));
			assertEquals("daf#afd", saveTest.getString("Database.Meta"));

			yamlText = saveTest.saveAsString();
		}
		try(YAML saveTest = new YAML(yamlText))
		{
			assertEquals("u\"s#er", saveTest.getString("Database.User"));
			assertEquals("P@SS#WORD", saveTest.getString("Database.Password"));
			assertEquals("local'host", saveTest.getString("Database.Host"));
			assertEquals("daf#afd", saveTest.getString("Database.Meta"));
		}
	}
}