# YAML-Parser
A YAML parser with support for comments.

## Features:

* Preserves comments on save
* Support for UTF-8, UTF-16 and UTF-32 files
* Correct handling of Windows style new lines (\r\n)

## Build:
### Requirements:

* git
* JDK for Java 1.8 or newer
* Maven 3

### Build it:
```
git clone https://github.com/MarkusWME/YAML-Parser.git
mvn clean install
```

## TODO:

* testing
* handling of multiline strings (block scalars): https://yaml-multiline.info/
* handling of keys with dots (should be merged into the correct location in the tree instead of creating their own)
* improve handling of quoted keys (reading)
* add map getter
    * `get<Type>Map(String key)` should return a map of type `Map<String, <Type>>` where each sub key is the key for the map and the sub keys value it's value
* full yaml 1.1 support https://yaml.org/spec/1.1/current.html
