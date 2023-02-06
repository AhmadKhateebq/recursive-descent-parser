import java.util.List;

public interface RESERVED {
    List<String> RELATIONAL_OP = List.of (
            "=",
            "<",
            ">",
            "<=",
            ">=",
            "<>"
    );
    List<String> RESERVED = List.of (
            "program",
            ";",
            "const",
            ":",
            ",",
            "integer",
            "char",
            "real",
            "var",
            "begin",
            "end",
            "(",
            ")",
            "+",
            "-",
            "*",
            "/",
            "div",
            "mod",
            "read",
            "readln",
            "writeln",
            "write",
            "if",
            "else",
            "then",
            "while",
            "do",
            "repeat",
            "until",
            ";"
    );
}
