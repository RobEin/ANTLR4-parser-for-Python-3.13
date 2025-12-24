import org.antlr.v4.runtime.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GRUN (Grammar Unit Test) for Python - Java 8 implementation
 */
public class grun4py {
    private static final String UTF8 = "utf-8";
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final byte[] UTF32_BE_BOM = {0x00, 0x00, (byte) 0xFE, (byte) 0xFF};
    private static final byte[] UTF32_LE_BOM = {(byte) 0xFF, (byte) 0xFE, 0x00, 0x00};
    private static final byte[] UTF16_BE_BOM = {(byte) 0xFE, (byte) 0xFF};
    private static final byte[] UTF16_LE_BOM = {(byte) 0xFF, (byte) 0xFE};
    private static final byte MAX_BOM_LENGTH = 4;

    private static final byte MAX_ASCII = 0x7f;

    private static final Pattern ENCODING_PATTERN =
    Pattern.compile("^[ \\t\\f]*#.*?coding[:=][ \\t]*([-_.a-zA-Z0-9]+)");
    private static final Pattern COMMENT_PATTERN =
    Pattern.compile("^[ \\t\\f]*(#.*)?$");

    private static final Map<String, String> ENCODING_MAP = new LinkedHashMap<>();

    static {
        // UTF-8
        ENCODING_MAP.put("utf8sig", "utf-8");
        ENCODING_MAP.put("utf8", "utf-8");
        ENCODING_MAP.put("utf", "utf-8");
        // UTF-16 LE
        ENCODING_MAP.put("utf16le", "utf-16LE");
        ENCODING_MAP.put("utf16", "utf-16LE");
        ENCODING_MAP.put("ucs2", "utf-16LE");
        ENCODING_MAP.put("ucs2le", "utf-16LE");
        // ISO-8859-1 / Latin-1
        ENCODING_MAP.put("latin1", "iso-8859-1");
        ENCODING_MAP.put("latin", "iso-8859-1");
        ENCODING_MAP.put("iso88591", "iso-8859-1");
        ENCODING_MAP.put("iso8859", "iso-8859-1");
        ENCODING_MAP.put("cp819", "iso-8859-1");
        // ASCII
        ENCODING_MAP.put("ascii", "us-ascii");
        ENCODING_MAP.put("usascii", "us-ascii");
        ENCODING_MAP.put("ansiX341968", "us-ascii");
        ENCODING_MAP.put("cp367", "us-ascii");
        // Deprecated alias
        ENCODING_MAP.put("binary", "iso-8859-1");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Please provide an input file path");
            System.exit(1);
        }

        final Path path = Paths.get(args[0]);

        try {
            final String encodingName = detectEncoding(path);
            final CharStream input = CharStreams.fromPath(path, Charset.forName(encodingName));

            final PythonLexer lexer = new PythonLexer(input);
            lexer.setEncodingName(encodingName); // generate ENCODING token

            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final PythonParser parser = new PythonParser(tokens);

            tokens.fill();
            for (final Token token : tokens.getTokens()) {
                System.out.println(formatToken(token));
            }

            parser.file_input();
            System.exit(parser.getNumberOfSyntaxErrors());
        } catch (final Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // ---------- Token formatting ----------

    private static String formatToken(final Token token) {
        final String tokenText = escapeSpecialChars(token.getText());
        final String tokenName = token.getType() == Token.EOF
                                 ? "EOF"
                                 : PythonLexer.VOCABULARY.getSymbolicName(token.getType());

        final String channelName = token.getChannel() == Token.DEFAULT_CHANNEL
                                   ? ""
                                   : String.format("channel=%d,", token.getChannel());

        return String.format("[@%d,%d:%d='%s',<%s>,%s%d:%d]",
                             token.getTokenIndex(),
                             token.getStartIndex(),
                             token.getStopIndex(),
                             tokenText,
                             tokenName,
                             channelName,
                             token.getLine(),
                             token.getCharPositionInLine());
    }

    private static String escapeSpecialChars(final String text) {
        return text
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t")
               .replace("\f", "\\f");
    }

    // ---------- Encoding detection ----------

    private static String detectEncoding(final Path path) throws IOException {
        final boolean hasUTF8BOM = detectUTF8BOM(path);
        final String commentEncoding = detectEncodingFromComments(path, hasUTF8BOM);
        return resolveFinalEncoding(path, hasUTF8BOM, commentEncoding);
    }

    private static boolean detectUTF8BOM(final Path path) throws IOException {
        final byte[] buffer = new byte[MAX_BOM_LENGTH];
        final int bytesRead;
        try (InputStream stream = Files.newInputStream(path)) {
            bytesRead = stream.read(buffer);
        }

        if (bufferStartsWith(buffer, bytesRead, UTF8_BOM)) return true;
        if (bufferStartsWith(buffer, bytesRead, UTF32_BE_BOM)) throw bomError(path, "UTF-32 BE BOM");
        if (bufferStartsWith(buffer, bytesRead, UTF32_LE_BOM)) throw bomError(path, "UTF-32 LE BOM");
        if (bufferStartsWith(buffer, bytesRead, UTF16_BE_BOM)) throw bomError(path, "UTF-16 BE BOM");
        if (bufferStartsWith(buffer, bytesRead, UTF16_LE_BOM)) throw bomError(path, "UTF-16 LE BOM");
        return false;
    }

    private static boolean bufferStartsWith(final byte[] buffer, final int bytesRead, final byte[] bom) {
        if (bytesRead < bom.length) return false; // Not enough bytes to match this BOM

        for (int i = 0; i < bom.length; i++) {
            if (buffer[i] != bom[i]) return false;
        }
        return true;
    }

    private static IOException bomError(final Path path, final String msg) {
        return new IOException("Invalid BOM encoding for '" + path.getFileName() + "': " + msg);
    }

    private static String detectEncodingFromComments(final Path path, final boolean hasUTF8BOM) throws IOException {
        try (InputStream stream = Files.newInputStream(path)) {
            if (hasUTF8BOM) stream.skip(UTF8_BOM.length);

            for (int i = 0; i < 2; i++) {
                final String line = readAsciiLine(stream);
                if (line == null) return "";

                if (COMMENT_PATTERN.matcher(line).matches()) {
                    final String enc = extractEncodingFromLine(line);
                    if (!enc.isEmpty()) {
                        return enc; // encoding found in comment
                    }
                } else {
                    break; // statement or backslash found (the line is not blank, not whitespace(s), not comment)
                }
            }
        }
        return "";
    }

    private static String readAsciiLine(final InputStream stream) throws IOException {
        final StringBuilder lineBuilder = new StringBuilder();
        int ascii;
        while ((ascii = stream.read()) != -1) {
            if (ascii > MAX_ASCII) return null;
            if (ascii == '\n') break;
            if (ascii != '\r') lineBuilder.append((char) ascii);
        }
        return lineBuilder.toString();
    }

    private static String extractEncodingFromLine(final String line) {
        final Matcher m = ENCODING_PATTERN.matcher(line);
        if (!m.find()) return "";
        return normalizeEncoding(m.group(1));
    }

    private static String normalizeEncoding(final String enc) {
        if (enc == null || enc.isEmpty()) return enc;

        final String normalized = enc
                                  .toLowerCase()
                                  .replaceAll("[_\\-\\s]", "")
                                  .replaceAll("codec$", "");

        return ENCODING_MAP.getOrDefault(normalized, enc);
    }

    private static String resolveFinalEncoding(final Path path,
                                               final boolean hasUTF8_BOM,
                                               final String commentEncodingName) throws IOException {

        final boolean hasConflict = !commentEncodingName.isEmpty()
                                    && hasUTF8_BOM
                                    && !isUTF8(commentEncodingName);
        if (hasConflict) {
            throw new IOException("Encoding problem for '" + path.getFileName() + "': utf-8 BOM");
        }
        return commentEncodingName.isEmpty() ? UTF8 : commentEncodingName;
    }

    private static boolean isUTF8(final String enc) {
        return enc.replaceAll("[\\-_]", "").toLowerCase().equals("utf8");
    }
}
