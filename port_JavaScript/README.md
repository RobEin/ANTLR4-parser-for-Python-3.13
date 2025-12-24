### JavaScript port

#### Prerequisites:
- Installed [ANTLR4-tools](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md#getting-started-the-easy-way-using-antlr4-tools)
- Installed [Node.js](https://nodejs.org/en/download/package-manager)
 

#### Command line example for Node.js:
- first download the necessary node modules and set the ```type``` field in the ```package.json``` then copy the two grammar files and example.py to this directory:
```bash
    npm install antlr4
    npm install iconv-lite
    npm install webpack
    npm pkg set type=module
```

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
antlr4 -Dlanguage=JavaScript PythonLexer.g4
antlr4 -Dlanguage=JavaScript PythonParser.g4
node grun4py.js example.py
```


#### Related link:
[ANTLR4 JavaScript target](https://github.com/antlr/antlr4/blob/dev/doc/javascript-target.md)
