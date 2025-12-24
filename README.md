# ANTLR4 parser for Python 3.14.2 &nbsp; [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

### About files:
 - PythonParser.g4 is the ANTLR4 parser grammar that based on the official [Python PEG grammar](https://docs.python.org/3.14/reference/grammar.html)

 - PythonLexerBase class
    - handles the Python indentations
    - creates encoding token
    - tokenizes f-string, t-string literals
    - and manage many other things

- Example files from: [Python 3.14 Standard Lib](https://github.com/python/cpython/tree/3.14/Lib)<br/><br/>

### Recent changes:
- refreshed the parser grammar for Python 3.14.2
- tokenizing t-string literals
- tokenizing BOM Unicode character at the start of the file so it is skipped in the token stream
- moved encoding detection from PythonLexerBase to a separate component (grun4py)

#### [Previous changes](https://github.com/RobEin/ANTLR4-parser-for-Python-3.14/blob/main/changes.md)<br/><br/> 
### Related links:
[ANTLR 4](https://www.antlr.org/)

[ANTLR4-tools](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md#getting-started-the-easy-way-using-antlr4-tools)

[ANTLR 4 Documentation](https://github.com/antlr/antlr4/tree/master/doc)

[ANTLR 4 Runtime API](https://www.antlr.org/api/Java/)

[Python 3.14 Lexical Analysis](https://docs.python.org/3.14/reference/lexical_analysis.html)

[cpython](https://github.com/python/cpython)

[ANTLR4 parser for Python 2.7.18](https://github.com/RobEin/ANTLR4-parser-for-Python-2.7.18)

[ANTLR4 parser for Python 3.8](https://github.com/RobEin/ANTLR4-parser-for-Python-3.8.12)
