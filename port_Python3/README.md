### Python 3 port

#### Prerequisites:
- Installed [ANTLR4-tools](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md#getting-started-the-easy-way-using-antlr4-tools)
- Installed [Python 3](https://www.python.org/downloads/)
- Installed [ANTLR4 Python 3 runtime](https://pypi.org/project/antlr4-python3-runtime/)


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
antlr4 -Dlanguage=Python3 PythonLexer.g4
antlr4 -Dlanguage=Python3 PythonParser.g4
python grun4py.py example.py
pygrun --tree Python file_input example.py
```


#### Related link:
[ANTLR4 Python 3 target](https://github.com/antlr/antlr4/blob/master/doc/python-target.md)
