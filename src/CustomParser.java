
public enum CustomParser {
    PROG_DECL {
        void parse(String s) throws ParseException {
            s = s.trim ();
            try {
                if (s.endsWith (".")) {
                    s = s.substring (0,s.length ()-1);
                    HEADING.parse (s.substring (0, s.indexOf (";")+1));
                    s = s.substring (s.indexOf (";")+1);
                    DECLARATION.parse (s.substring (0,s.indexOf ("begin")));
                    s = s.substring (s.indexOf ("begin"));
                    BLOCK.parse (s);
                } else {
                    throw new ParseException ("Illegal begin/end");
                }
            }catch (StringIndexOutOfBoundsException e) {
                throw new ParseException ("Missing begin/end");
            }

        }
    },
    HEADING {
        void parse(String s) throws ParseException {
            String[] parts = s.trim ().split (" ");
            if (parts[0].equals ("program")) {
                if (!parts[1].endsWith (";") && !parts[2].equals (";"))
                    throw new ParseException ("Illegal Heading");
            }
        }
    },
    DECLARATION {
        void parse(String s) throws ParseException {
            if (s.length () != 0) {
                int c = s.indexOf ("const");
                int v = s.indexOf ("var");
                if (v != -1)
                    VAR_LIST.parse (s.substring (v));
                if (c != -1)
                    if (v != -1)
                        CONST_DECL.parse (s.substring (c, v));
                    else
                        CONST_DECL.parse (s);

                if (c == -1 && v == -1)
                    throw new ParseException ("Declaration Expected");
            }


        }
    },
    VALUE {
        void parse(String s) throws ParseException {
            try {
                Integer.parseInt (s);
            } catch (NumberFormatException e) {
                Double.parseDouble (s);
            } catch (Exception e1) {
                throw new ParseException ("Number Value Expected");
            }
        }
    },
    CONST_LIST {
        void parse(String s) throws ParseException {
            int eq = s.indexOf ('=');
            int st = s.indexOf (';');
            String value = s.trim ().substring (eq + 1, st);
            VALUE.parse (value);
            s = s.trim ().substring (st + 1);
            if (s.length () != 0) {
                if (!s.endsWith (";"))
                    throw new ParseException ("Expected ;");
                CONST_LIST.parse (s);
            }

        }
    },
    CONST_DECL {
        void parse(String s) throws ParseException {
            if (s.length () != 0) {
                String[] parts = s.trim ().split (" ");
                if (parts[0].equals ("const"))
                    CONST_LIST.parse (parts[1]);
                else throw new ParseException ("const Expected");
            }
        }
    },
    NAME_LIST {
        void parse(String s) throws ParseException {
            String[] parts = s.trim ().split (",");
            for (String part : parts) {
                MORE_NAMES.parse (part);
            }
        }
    },
    MORE_NAMES {
        void parse(String s) throws ParseException {
            if (s.length () == 0)
                throw new ParseException ("Var Name Expected");
        }
    },
    DATA_TYPE {
        void parse(String s) throws ParseException {
            if (!s.equals ("integer"))
                if (!s.equals ("real"))
                    if (!s.equals ("char"))
                        throw new ParseException ("Data Type Expected");
        }
    },
    VAR_ITEM {
        void parse(String s) throws ParseException {
            String[] parts = s.split (":");
            NAME_LIST.parse (parts[0]);
            DATA_TYPE.parse (parts[1]);
        }
    },
    VAR_LIST {
        void parse(String s) throws ParseException {
            if (s.contains (";")){
                String varItem = s.trim ().substring (s.indexOf (";"));
                s = s.trim ().substring (s.indexOf (";")+1);
                VAR_ITEM.parse (varItem);
                VAR_LIST.parse (s);
            }
        }
    },
    VAR_DECL {
        void parse(String s) throws ParseException {
            if (s.length () != 0) {
                if (s.startsWith ("var")) {
                    VAR_LIST.parse (s.substring (3));
                } else throw new ParseException ("var Expected");
            }
        }
    },
    NAME_VALUE {
        void parse(String s) throws ParseException {
            if (!RESERVED.RESERVED.contains (s)) {
                if (s.toLowerCase ().equals (s.toUpperCase ()))
                    VALUE.parse (s);
            } else throw new ParseException ("Expected var,const or value");
        }
    },
    EXP_PRIME {
        void parse(String s) throws ParseException {
            if (s.startsWith ("+") || s.startsWith ("-")) {
                EXP.parse (s);
            } else if (s.length () != 0)
                throw new ParseException ("Expected var,const or value");
        }
    },
    EXP {
        void parse(String s) throws ParseException {
            int max = Math.max (s.indexOf ("+"), s.indexOf ("-"));
            if (max != -1) {
                TERM.parse (s.substring (0, max));
                EXP_PRIME.parse (s.substring (max));
            } else if (s.length () != 0)
                TERM.parse (s);
            else
                throw new ParseException ("Term Expected");
        }
    },
    TERM {
        @Override
        void parse(String s) throws ParseException {
            int m = s.indexOf ("*");
            int mod = s.indexOf ("mod");
            int dev = s.indexOf ("dev");
            int d = s.indexOf ("/");
            int max = m;
            max = Math.max (max, mod);
            max = Math.max (max, dev);
            max = Math.max (max, d);
            if (max != -1) {
                FACTOR.parse (s.substring (0, max));
                TERM_PRIME.parse (s.substring (max));
            } else {
                FACTOR.parse (s);
            }
        }
    },
    TERM_PRIME {
        void parse(String s) throws ParseException {
            if (s.startsWith ("*") || s.startsWith ("/"))
                s = s.substring (1);
            if (s.startsWith ("mod") || s.startsWith ("div"))
                s = s.substring (3);
            FACTOR.parse (s);
            while (s.length () != 0)
                TERM_PRIME.parse (s);
            //exp_prime
        }
    },
    FACTOR {
        void parse(String s) throws ParseException {
            if (s.startsWith ("(") && s.endsWith (")"))
                EXP.parse (s.substring (1, s.length () - 1));
            else
                NAME_VALUE.parse (s);
        }
    },
    ASS_STMT {
        void parse(String s) throws ParseException {
            int i = s.indexOf (":=");
            s = s.trim ().substring (i + 2);
            if (s.length () != 0)
                EXP.parse (s);
            else
                throw new ParseException ("Expression Expected");
        }
    },
    READ_STMT {
        void parse(String s) throws ParseException {
            if (!s.endsWith (")"))
                throw new ParseException ("Ending ) is expected");
            if (s.startsWith ("read"))
                s = s.substring (5, s.length () - 1);
            if (s.startsWith ("readln"))
                s = s.substring (7, s.length () - 1);
            NAME_LIST.parse (s);
        }
    },
    WRITE_STMT {
        void parse(String s) throws ParseException {
            if (!s.endsWith (")"))
                throw new ParseException ("Ending ) is expected");
            if (s.startsWith ("write"))
                s = s.substring (6, s.length () - 1);
            if (s.startsWith ("writeln"))
                s = s.substring (8, s.length () - 1);
            NAME_LIST.parse (s);
        }
    },
    WHILE_STMT {
        void parse(String s) throws ParseException {
            if (s.startsWith ("while")) {
                s = s.substring (5);
                if (s.contains ("do")) {
                    String[] parts = s.trim ().split ("do");
                    CONDITION.parse (parts[0]);
                    STATEMENT.parse (parts[1]);
                } else throw new ParseException ("do is expected here");
            }
            throw new ParseException ("while is expected here");
        }
    },
    REPEAT_STMT {
        void parse(String s) throws ParseException {
            if (s.startsWith ("repeat")) {
                s = s.substring (6);
                if (s.contains ("until")) {
                    String[] parts = s.trim ().split ("until");
                    STMT_LIST.parse (parts[0]);
                    CONDITION.parse (parts[1]);
                } else throw new ParseException ("do is expected here");
            }
            throw new ParseException ("repeat is expected here");
        }
    },
    IF_STMT {
        void parse(String s) throws ParseException {
            if (s.startsWith ("if") && s.contains ("then")) {
                s = s.trim ().substring (2);
                String[] parts = s.split ("then");
                CONDITION.parse (parts[0]);
                STATEMENT.parse (parts[1]);
                if (parts.length > 2) {
                    s = parts[3];
                    if (s.startsWith ("else")) {
                        STATEMENT.parse (s.substring (4));
                    } else throw new ParseException ("else is expected");
                }
            } else throw new ParseException ("if is expected ");
        }
    },
    CONDITION {
        void parse(String s) throws ParseException {
            String[] parts;
            if (s.contains (">="))
                parts = s.split (">=");
            else if (s.contains ("<="))
                parts = s.split ("<=");
            else if (s.contains ("<>"))
                parts = s.split ("<>");
            else if (s.contains (">"))
                parts = s.split (">");
            else if (s.contains ("<"))
                parts = s.split ("<");
            else if (s.contains ("="))
                parts = s.split ("=");
            else
                throw new ParseException ("if is expected ");
            NAME_VALUE.parse (parts[0]);
            NAME_VALUE.parse (parts[1]);
        }
    },
    STATEMENT {
        void parse(String s) throws ParseException {
            if (s.startsWith ("read"))
                READ_STMT.parse (s);
            else if (s.startsWith ("write"))
                WRITE_STMT.parse (s);
            else if (s.startsWith ("if"))
                IF_STMT.parse (s);
            else if (s.startsWith ("while"))
                WHILE_STMT.parse (s);
            else if (s.startsWith ("repeat"))
                REPEAT_STMT.parse (s);
            else if (s.startsWith ("begin"))
                BLOCK.parse (s);
            else if (s.contains (":="))
                ASS_STMT.parse (s);
            else throw new ParseException ("STATEMENT EXPECTED");

        }
    },
    STMT_LIST {
        void parse(String s) throws ParseException {
            String [] parts = s.split (";");
            for (String part : parts) {
                STATEMENT.parse (part);
            }
        }
    },
    BLOCK {
        void parse(String s) throws ParseException {
            if (s.startsWith ("begin") && s.endsWith ("end")){
                STMT_LIST.parse (s.substring (5,s.length ()-3));
            }else throw new ParseException ("ILLEGAL begin/end");
        }
    },
    RELATIONAL_OP {
        void parse(String s) throws ParseException {
            if (!RESERVED.RELATIONAL_OP.contains (s)) {
                throw new ParseException ("Illegal variable: " + s);
            }
        }
    };


    abstract void parse(String s) throws ParseException;

    public static void main(String[] args) throws ParseException {
                    PROG_DECL.parse (
                            "program name ;\n" +
                    "begin total = var1 + var2; \n" +
                    "while (var1 < var2) \n" +
                    "while ( var3 > var4)\n" +
                    "var2 = var2 - var1 \n" +
                    "end .");
        System.out.println ("OK");
    }
    public static boolean parseCode(String s) throws ParseException {
        PROG_DECL.parse (s);
        return true;
    }
}


class ParseException extends Exception {
    public ParseException(String message) {
        super (message);
    }
}