### Java 8 port

#### Prerequisites:
- Installed [ANTLR4-tools](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md#getting-started-the-easy-way-using-antlr4-tools)


#### Command line example:
- first copy the two grammar files and the example.py to this directory:

Linux:
```bash
    cp ../*.g4
    cp ../example.py
```

Windows:
```bash
    copy ..\*.g4
    copy ..\example.py
```

```bash
antlr4 PythonLexer.g4
antlr4 PythonParser.g4
javac *.java
java grun4py.java example.py
grun Python file_input -gui example.py
```

 
#### Related link:
[ANTLR4 Java target](https://github.com/antlr/antlr4/blob/master/doc/java-target.md)
