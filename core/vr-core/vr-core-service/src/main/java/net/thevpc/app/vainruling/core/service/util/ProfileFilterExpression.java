/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * @author taha.bensalah@gmail.com
 */
public class ProfileFilterExpression {

    private String mainExpression;
    private String profileListExpression;
    private String filterExpression;

    public ProfileFilterExpression(String profileListExpression, String filterExpression) {
        this(
                (profileListExpression == null ? "" : profileListExpression)
                        + " where "
                        + (filterExpression == null ? "" : filterExpression),
                profileListExpression,
                filterExpression
        );
    }

    public ProfileFilterExpression(String mainExpression, String profileListExpression, String filterExpression) {
        if (mainExpression == null) {
            mainExpression = "";
        }
        if (profileListExpression == null) {
            profileListExpression = "";
        }
        if (filterExpression == null) {
            filterExpression = "";
        }
        this.mainExpression = mainExpression;
        this.profileListExpression = profileListExpression;
        this.filterExpression = filterExpression;
        profileListExpression = validateProfileListExpr(profileListExpression,true,null);
    }

    public ProfileFilterExpression(String mainExpression) {
        if (mainExpression == null) {
            mainExpression = "";
        }
        this.mainExpression = mainExpression;
        filterExpression = "";
        profileListExpression = mainExpression.trim();
        int fromIndex = 0;
        String profilePatternLower = profileListExpression.toLowerCase();
        int whereLength = "where".length();
        int profilePatternLowerLength = profilePatternLower.length();
        while (fromIndex < profilePatternLowerLength) {
            int i = profilePatternLower.indexOf("where", fromIndex);
            if (i < 0) {
                break;
            }
            if (i == 0) {
                String after = profileListExpression.substring(i + whereLength);
                if (after.trim().length() == 0) {
                    //just ignore where clause
                    profileListExpression = "";
                    break;
                } else if (!Character.isJavaIdentifierPart(after.charAt(0))) {
                    profileListExpression = "";
                    filterExpression = after.trim();
                    break;
                } else {
                    fromIndex = i + whereLength;
                    //just ignore it
                }
            } else {
                if (!Character.isJavaIdentifierPart(profileListExpression.charAt(i - 1))) {
                    String after = profileListExpression.substring(i + whereLength);
                    if (after.trim().length() == 0) {
                        //just ignore where clause
                        profileListExpression = profileListExpression.substring(0, i).trim();
                        break;
                    } else if (!Character.isJavaIdentifierPart(after.charAt(0))) {
                        profileListExpression = profileListExpression.substring(0, i).trim();
                        filterExpression = after.trim();
                        break;
                    } else {
                        fromIndex = i + whereLength;
                        //just ignore it
                    }
                } else {
                    fromIndex = i + whereLength;
                }
            }
        }
        profileListExpression = validateProfileListExpr(profileListExpression,true,null);
    }

//    public static void main(String[] args) {
//        String str = "( ( h)) , tt";
////        String str = " ('taha  hammadi' ) ( ( h))";
//        System.out.println(validateProfileListExpr(str,true,null));
//    }

    public static String validateProfileListExpr(String str,boolean trim,ProfileStringConverter converter) {
        if (str == null) {
            str = "";
        }
        StreamTokenizer st = new StreamTokenizer(new StringReader(str));
        st.resetSyntax();
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.wordChars('0', '9');
        st.wordChars('_', '_');
        st.wordChars(128 + 32, 255);
        if(trim) {
        st.whitespaceChars(0, ' ');
        }
        st.quoteChar('"');
        st.quoteChar('\'');

        StringBuilder sb = new StringBuilder();
        int token = -1;
        try {
            int lastNonSpace = -1;
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                    case StreamTokenizer.TT_WORD: {
                        if (lastNonSpace==StreamTokenizer.TT_WORD || lastNonSpace==')' ) {
                            sb.append(",");
                        }
                        String sval = st.sval;
                        if(converter!=null){
                            sval=converter.convert(sval);
                        }
                        sb.append(sval);
                        lastNonSpace = token;
                        break;
                    }
                    case ' ': {
                        sb.append((char) token);
                        break;
                    }
                    case '\'':
                    case '\"': {
                        sb.append((char) token);
                        if (lastNonSpace==StreamTokenizer.TT_WORD || lastNonSpace==')' ) {
                            sb.append(",");
                        }
                        String sval = st.sval;
                        if(converter!=null){
                            sval=converter.convert(sval);
                        }
                        sb.append(sval);
                        sb.append((char) token);
                        lastNonSpace = token;
                        break;
                    }
                    case '(': {
                        if (lastNonSpace==StreamTokenizer.TT_WORD || lastNonSpace==')' ) {
                            sb.append(",");
                        }
                        sb.append((char) token);
                        lastNonSpace = token;
                        break;
                    }
                    default: {
                        sb.append((char) token);
                        lastNonSpace=token;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public interface ProfileStringConverter{
        public String convert(String s);
    }

    public String getMainExpression() {
        return mainExpression;
    }

    public String getProfileListExpression() {
        return profileListExpression;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    @Override
    public String toString() {
        return "ProfileFilterExpression{" + "mainExpression=\"" + mainExpression + "\", profileListExpression=\"" + profileListExpression + "\", filterExpression=\"" + filterExpression + "\"}";
    }

}
