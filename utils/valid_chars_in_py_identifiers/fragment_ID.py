"""Generate ANTLR4 grammar fragments for Python Unicode identifiers."""

import sys
from pathlib import Path
from typing import List


def main() -> None:
    """Generate and save ANTLR4 ID_START and ID_CONTINUE fragments."""
    print("main")
    python_version = f"for Python {sys.version_info.major}.{sys.version_info.minor}.{sys.version_info.micro}"
    
    start_codes = []
    continue_codes = []

    for i in range(sys.maxunicode):
        c = chr(i)
        if c.isidentifier():
            start_codes.append(i)
        else:
            id = 'a' + c
            if id.isidentifier():
                continue_codes.append(i)

    
    continue_fragment = _build_continue_fragment(continue_codes, python_version)
    start_fragment = _build_start_fragment(start_codes, python_version)
    
    output = f"{continue_fragment}\n\n{start_fragment}"
    
    print(output)
    _save_to_file(output, "fragment_ID.txt")


def _build_continue_fragment(continue_codes: List[int], python_version: str) -> str:
    """Build the ID_CONTINUE grammar fragment."""
    ranged_unicodes = _format_as_ranges(continue_codes)
    
    return (
        f"fragment ID_CONTINUE // {python_version}\n"
        f"    : ID_START\n"
        f"    | {ranged_unicodes}\n"
        f"    ;"
    )


def _build_start_fragment(start_codes: List[int], python_version: str) -> str:
    """Build the ID_START grammar fragment."""
    ranged_unicodes = _format_as_ranges(start_codes)
    
    return (
        f"fragment ID_START // {python_version}\n"
        f"    : {ranged_unicodes}\n"
        f"    ;"
    )


def _format_as_ranges(unicodes: List[int]) -> str:
    """Convert list of Unicode code points into ANTLR4 hex ranges.
    Consecutive code points are merged into ranges (e.g., 'a'..'z').
    """
    if not unicodes:
        return ""
    
    ranged_list = []
    range_start = unicodes[0]
    range_end = unicodes[0]
    
    for code in unicodes[1:]:
        # Check if this code is consecutive to the current range
        if code == range_end + 1:
            range_end = code
        else:
            # Non-consecutive: finalize the current range
            ranged_list.append(_format_range(range_start, range_end))
            range_start = code
            range_end = code
    
    # Finalize the last range
    ranged_list.append(_format_range(range_start, range_end))
    
    return "\n    | ".join(ranged_list)


def _format_range(start: int, end: int) -> str:
    """Format a Unicode range as ANTLR4 hex literal(s).
    
    Args:
        start: Start code point of the range.
        end: End code point of the range.
    """
    start_hex = _to_antlr4_hex(start)
    
    if start == end:
        return start_hex
    
    end_hex = _to_antlr4_hex(end)
    return f"{start_hex} .. {end_hex}"


def _to_antlr4_hex(code: int) -> str:
    """Convert Unicode code point to ANTLR4 hex escape sequence."""
    return f"'\\u{{{code:04X}}}'"


def _save_to_file(content: str, filename: str) -> None:
    """Save content to file."""
    Path(filename).write_text(content, encoding="utf-8")


if __name__ == "__main__":
    main()
