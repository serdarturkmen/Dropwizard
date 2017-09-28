package com.serdar.comodo.db.util;

import java.util.HashMap;

public class WhereClause {
    public HashMap<String, Object> queryValues; // [<"foo","bar">, <"baz","taz">]
    public String preparedString; // "WHERE foo=:foo AND bar=:baz"
}