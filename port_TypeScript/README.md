### TypeScript port

#### Prerequisites:
- Installed [ANTLR4-tools](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md#getting-started-the-easy-way-using-antlr4-tools)
- Installed [Node.js](https://nodejs.org/en/download/package-manager)
 

#### Command line example:
- first download the necessary node modules and create the ```tsconfig.json``` then copy the two grammar files and example.py to this directory:
```bash
    npm install typescript
    npm install typescript-collections
    npm install iconv-lite
    npm install antlr4
    npm install webpack
    npm install --save-dev @types/node
    npx tsc --init --module commonjs --types node --exactOptionalPropertyTypes false --verbatimModuleSyntax false --esModuleInterop true

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
antlr4 -Dlanguage=TypeScript PythonLexer.g4
antlr4 -Dlanguage=TypeScript PythonParser.g4
npx tsc
node grun4py.js example.py
```


#### Related link:
[ANTLR4 TypeScript target](https://github.com/antlr/antlr4/blob/dev/doc/typescript-target.md)
