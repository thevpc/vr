package net.vpc.app.vainruling.service.test;

import junit.framework.TestCase;
import net.vpc.app.vainruling.core.service.util.DiffHtmlStyle;
import net.vpc.app.vainruling.core.service.util.GoogleDiffMatchPatch;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by vpc on 9/10/16.
 */

public class TestDiff {

    private GoogleDiffMatchPatch dmp;
    private GoogleDiffMatchPatch.Operation DELETE = GoogleDiffMatchPatch.Operation.DELETE;
    private GoogleDiffMatchPatch.Operation EQUAL = GoogleDiffMatchPatch.Operation.EQUAL;
    private GoogleDiffMatchPatch.Operation INSERT = GoogleDiffMatchPatch.Operation.INSERT;

    @Before
    public void setUp() {
        // Create an instance of the diff_match_patch object.
        dmp = new GoogleDiffMatchPatch();
    }
    @Test
    public void diffMain(){
        String phrase1="1391;MsGT1-S2-?-Modélisation et Simulation-?-JBHT-C;Jamel Bel Hadj Taher;1.0;1.0\n" +
                "1393;MsGT1-S2-?-Réseaux de télécoms-?-C;Aref Meddeb;1.0;1.0\n" +
                "1398;MsH4M1.2-S2-?-Systèmes de Télécoms aux services du Transport-MsH4M1.2-C;Aref Meddeb;1.0;1.0\n" +
                "1399;MsII-S2-?-Coordination Master II-?-COORD;Hassen Mekki;1.0;1.0\n" +
                "1402;MsIIC-S2-?-Coordination Master IIC-?-COORD;Walid Chainbi;1.0;1.0\n";
        String phrase2="1391;MsGT1-S2-?-Modélisation et Simulation-?-JBHT-C;Jamel Bel Hadj Taher;1.0;1.0\n" +
                "1393;MsGT1-S2-?-Réseaux de télécoms-?-C;Aref Meddeb;1.0;1.0\n" +
                "1101;IA1-S1-1i9-Microprocesseurs et Assembleur-IA1.1-PM;Manel Abdel Hedi;2.0;1.0\n" +
                "1398;MsH4M1.2-S2-?-Systèmes de Télécoms aux services du Transport-MsH4M1.2-C;Taha Ben Salah;1.0;1.0\n" +
                "1399;MsII-S2-?-Coordination Master II-?-COORD;Hassen Mekki;1.0;1.0\n" +
                "1402;MsIIC-S2-?-Coordination Master IIC-?-COORD;Walid Chainbi;1.0;1.0\n";
        for (GoogleDiffMatchPatch.Diff diff : dmp.diff_main(phrase1, phrase2)) {
            System.out.println(diff);
        }

        System.out.println(VrUtils.diffToHtml(phrase1,phrase2,new DiffHtmlStyle().setFullPage(true)));

        Assert.assertEquals("ok","ok");
    }
    @Test
    public void testDiffCommonPrefix() {
        // Detect any common prefix.
        Assert.assertEquals("diff_commonPrefix: Null case.", 0, dmp.diff_commonPrefix("abc", "xyz"));

        Assert.assertEquals("diff_commonPrefix: Non-null case.", 4, dmp.diff_commonPrefix("1234abcdef", "1234xyz"));

        Assert.assertEquals("diff_commonPrefix: Whole case.", 4, dmp.diff_commonPrefix("1234", "1234xyz"));
    }

    @Test
    public void testDiffCommonSuffix() {
        // Detect any common suffix.
        Assert.assertEquals("diff_commonSuffix: Null case.", 0, dmp.diff_commonSuffix("abc", "xyz"));

        Assert.assertEquals("diff_commonSuffix: Non-null case.", 4, dmp.diff_commonSuffix("abcdef1234", "xyz1234"));

        Assert.assertEquals("diff_commonSuffix: Whole case.", 4, dmp.diff_commonSuffix("1234", "xyz1234"));
    }

//    public void testDiffCommonOverlap() {
//        // Detect any suffix/prefix overlap.
//        assertEquals("diff_commonOverlap: Null case.", 0, dmp.diff_commonOverlap("", "abcd"));
//
//        assertEquals("diff_commonOverlap: Whole case.", 3, dmp.diff_commonOverlap("abc", "abcd"));
//
//        assertEquals("diff_commonOverlap: No overlap.", 0, dmp.diff_commonOverlap("123456", "abcd"));
//
//        assertEquals("diff_commonOverlap: Overlap.", 3, dmp.diff_commonOverlap("123456xxx", "xxxabcd"));
//
//        // Some overly clever languages (C#) may treat ligatures as equal to their
//        // component letters.  E.g. U+FB01 == 'fi'
//        assertEquals("diff_commonOverlap: Unicode.", 0, dmp.diff_commonOverlap("fi", "\ufb01i"));
//    }
//
//    public void testDiffHalfmatch() {
//        // Detect a halfmatch.
//        dmp.Diff_Timeout = 1;
//        assertNull("diff_halfMatch: No match #1.", dmp.diff_halfMatch("1234567890", "abcdef"));
//
//        assertNull("diff_halfMatch: No match #2.", dmp.diff_halfMatch("12345", "23"));
//
//        assertArrayEquals("diff_halfMatch: Single Match #1.", new String[]{"12", "90", "a", "z", "345678"}, dmp.diff_halfMatch("1234567890", "a345678z"));
//
//        assertArrayEquals("diff_halfMatch: Single Match #2.", new String[]{"a", "z", "12", "90", "345678"}, dmp.diff_halfMatch("a345678z", "1234567890"));
//
//        assertArrayEquals("diff_halfMatch: Single Match #3.", new String[]{"abc", "z", "1234", "0", "56789"}, dmp.diff_halfMatch("abc56789z", "1234567890"));
//
//        assertArrayEquals("diff_halfMatch: Single Match #4.", new String[]{"a", "xyz", "1", "7890", "23456"}, dmp.diff_halfMatch("a23456xyz", "1234567890"));
//
//        assertArrayEquals("diff_halfMatch: Multiple Matches #1.", new String[]{"12123", "123121", "a", "z", "1234123451234"}, dmp.diff_halfMatch("121231234123451234123121", "a1234123451234z"));
//
//        assertArrayEquals("diff_halfMatch: Multiple Matches #2.", new String[]{"", "-=-=-=-=-=", "x", "", "x-=-=-=-=-=-=-="}, dmp.diff_halfMatch("x-=-=-=-=-=-=-=-=-=-=-=-=", "xx-=-=-=-=-=-=-="));
//
//        assertArrayEquals("diff_halfMatch: Multiple Matches #3.", new String[]{"-=-=-=-=-=", "", "", "y", "-=-=-=-=-=-=-=y"}, dmp.diff_halfMatch("-=-=-=-=-=-=-=-=-=-=-=-=y", "-=-=-=-=-=-=-=yy"));
//
//        // Optimal diff would be -q+x=H-i+e=lloHe+Hu=llo-Hew+y not -qHillo+x=HelloHe-w+Hulloy
//        assertArrayEquals("diff_halfMatch: Non-optimal halfmatch.", new String[]{"qHillo", "w", "x", "Hulloy", "HelloHe"}, dmp.diff_halfMatch("qHilloHelloHew", "xHelloHeHulloy"));
//
//        dmp.Diff_Timeout = 0;
//        assertNull("diff_halfMatch: Optimal no halfmatch.", dmp.diff_halfMatch("qHilloHelloHew", "xHelloHeHulloy"));
//    }

//    public void testDiffLinesToChars() {
//        // Convert lines down to characters.
//        ArrayList<String> tmpVector = new ArrayList<String>();
//        tmpVector.add("");
//        tmpVector.add("alpha\n");
//        tmpVector.add("beta\n");
//        assertLinesToCharsResultEquals("diff_linesToChars: Shared lines.", new GoogleDiffMatchPatch.LinesToCharsResult("\u0001\u0002\u0001", "\u0002\u0001\u0002", tmpVector), dmp.diff_linesToChars("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"));
//
//        tmpVector.clear();
//        tmpVector.add("");
//        tmpVector.add("alpha\r\n");
//        tmpVector.add("beta\r\n");
//        tmpVector.add("\r\n");
//        assertLinesToCharsResultEquals("diff_linesToChars: Empty string and blank lines.", new GoogleDiffMatchPatch.LinesToCharsResult("", "\u0001\u0002\u0003\u0003", tmpVector), dmp.diff_linesToChars("", "alpha\r\nbeta\r\n\r\n\r\n"));
//
//        tmpVector.clear();
//        tmpVector.add("");
//        tmpVector.add("a");
//        tmpVector.add("b");
//        assertLinesToCharsResultEquals("diff_linesToChars: No linebreaks.", new GoogleDiffMatchPatch.LinesToCharsResult("\u0001", "\u0002", tmpVector), dmp.diff_linesToChars("a", "b"));
//
//        // More than 256 to reveal any 8-bit limitations.
//        int n = 300;
//        tmpVector.clear();
//        StringBuilder lineList = new StringBuilder();
//        StringBuilder charList = new StringBuilder();
//        for (int x = 1; x < n + 1; x++) {
//            tmpVector.add(x + "\n");
//            lineList.append(x + "\n");
//            charList.append(String.valueOf((char) x));
//        }
//        assertEquals(n, tmpVector.size());
//        String lines = lineList.toString();
//        String chars = charList.toString();
//        assertEquals(n, chars.length());
//        tmpVector.add(0, "");
//        assertLinesToCharsResultEquals("diff_linesToChars: More than 256.", new GoogleDiffMatchPatch.LinesToCharsResult(chars, "", tmpVector), dmp.diff_linesToChars(lines, ""));
//    }

//    public void testDiffCharsToLines() {
//        // First check that Diff equality works.
//        assertTrue("diff_charsToLines: Equality #1.", new GoogleDiffMatchPatch.Diff(EQUAL, "a").equals(new GoogleDiffMatchPatch.Diff(EQUAL, "a")));
//
//        assertEquals("diff_charsToLines: Equality #2.", new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"));
//
//        // Convert chars up to lines.
//        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "\u0001\u0002\u0001"), new GoogleDiffMatchPatch.Diff(INSERT, "\u0002\u0001\u0002"));
//        ArrayList<String> tmpVector = new ArrayList<String>();
//        tmpVector.add("");
//        tmpVector.add("alpha\n");
//        tmpVector.add("beta\n");
//        dmp.diff_charsToLines(diffs, tmpVector);
//        assertEquals("diff_charsToLines: Shared lines.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "alpha\nbeta\nalpha\n"), new GoogleDiffMatchPatch.Diff(INSERT, "beta\nalpha\nbeta\n")), diffs);
//
//        // More than 256 to reveal any 8-bit limitations.
//        int n = 300;
//        tmpVector.clear();
//        StringBuilder lineList = new StringBuilder();
//        StringBuilder charList = new StringBuilder();
//        for (int x = 1; x < n + 1; x++) {
//            tmpVector.add(x + "\n");
//            lineList.append(x + "\n");
//            charList.append(String.valueOf((char) x));
//        }
//        assertEquals(n, tmpVector.size());
//        String lines = lineList.toString();
//        String chars = charList.toString();
//        assertEquals(n, chars.length());
//        tmpVector.add(0, "");
//        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, chars));
//        dmp.diff_charsToLines(diffs, tmpVector);
//        assertEquals("diff_charsToLines: More than 256.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, lines)), diffs);
//    }

    @Test
    public void testDiffCleanupMerge() {
        // Cleanup a messy diff.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList();
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Null case.", diffList(), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "b"), new GoogleDiffMatchPatch.Diff(INSERT, "c"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: No change case.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "b"), new GoogleDiffMatchPatch.Diff(INSERT, "c")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "b"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Merge equalities.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "abc")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "b"), new GoogleDiffMatchPatch.Diff(DELETE, "c"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Merge deletions.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "b"), new GoogleDiffMatchPatch.Diff(INSERT, "c"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Merge insertions.", diffList(new GoogleDiffMatchPatch.Diff(INSERT, "abc")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "b"), new GoogleDiffMatchPatch.Diff(DELETE, "c"), new GoogleDiffMatchPatch.Diff(INSERT, "d"), new GoogleDiffMatchPatch.Diff(EQUAL, "e"), new GoogleDiffMatchPatch.Diff(EQUAL, "f"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Merge interweave.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ac"), new GoogleDiffMatchPatch.Diff(INSERT, "bd"), new GoogleDiffMatchPatch.Diff(EQUAL, "ef")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "abc"), new GoogleDiffMatchPatch.Diff(DELETE, "dc"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Prefix and suffix detection.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "d"), new GoogleDiffMatchPatch.Diff(INSERT, "b"), new GoogleDiffMatchPatch.Diff(EQUAL, "c")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "x"), new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "abc"), new GoogleDiffMatchPatch.Diff(DELETE, "dc"), new GoogleDiffMatchPatch.Diff(EQUAL, "y"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Prefix and suffix detection with equalities.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "xa"), new GoogleDiffMatchPatch.Diff(DELETE, "d"), new GoogleDiffMatchPatch.Diff(INSERT, "b"), new GoogleDiffMatchPatch.Diff(EQUAL, "cy")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "ba"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Slide edit left.", diffList(new GoogleDiffMatchPatch.Diff(INSERT, "ab"), new GoogleDiffMatchPatch.Diff(EQUAL, "ac")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "c"), new GoogleDiffMatchPatch.Diff(INSERT, "ab"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Slide edit right.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "ca"), new GoogleDiffMatchPatch.Diff(INSERT, "ba")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "b"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"), new GoogleDiffMatchPatch.Diff(DELETE, "ac"), new GoogleDiffMatchPatch.Diff(EQUAL, "x"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Slide edit left recursive.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(EQUAL, "acx")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "x"), new GoogleDiffMatchPatch.Diff(DELETE, "ca"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"), new GoogleDiffMatchPatch.Diff(DELETE, "b"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"));
        dmp.diff_cleanupMerge(diffs);
        Assert.assertEquals("diff_cleanupMerge: Slide edit right recursive.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "xca"), new GoogleDiffMatchPatch.Diff(DELETE, "cba")), diffs);
    }

    @Test
    public void testDiffCleanupSemanticLossless() {
        // Slide diffs to match logical boundaries.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList();
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Null case.", diffList(), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "AAA\r\n\r\nBBB"), new GoogleDiffMatchPatch.Diff(INSERT, "\r\nDDD\r\n\r\nBBB"), new GoogleDiffMatchPatch.Diff(EQUAL, "\r\nEEE"));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Blank lines.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "AAA\r\n\r\n"), new GoogleDiffMatchPatch.Diff(INSERT, "BBB\r\nDDD\r\n\r\n"), new GoogleDiffMatchPatch.Diff(EQUAL, "BBB\r\nEEE")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "AAA\r\nBBB"), new GoogleDiffMatchPatch.Diff(INSERT, " DDD\r\nBBB"), new GoogleDiffMatchPatch.Diff(EQUAL, " EEE"));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Line boundaries.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "AAA\r\n"), new GoogleDiffMatchPatch.Diff(INSERT, "BBB DDD\r\n"), new GoogleDiffMatchPatch.Diff(EQUAL, "BBB EEE")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The c"), new GoogleDiffMatchPatch.Diff(INSERT, "ow and the c"), new GoogleDiffMatchPatch.Diff(EQUAL, "at."));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Word boundaries.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The "), new GoogleDiffMatchPatch.Diff(INSERT, "cow and the "), new GoogleDiffMatchPatch.Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The-c"), new GoogleDiffMatchPatch.Diff(INSERT, "ow-and-the-c"), new GoogleDiffMatchPatch.Diff(EQUAL, "at."));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Alphanumeric boundaries.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The-"), new GoogleDiffMatchPatch.Diff(INSERT, "cow-and-the-"), new GoogleDiffMatchPatch.Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "ax"));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Hitting the start.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "aax")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "xa"), new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Hitting the end.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "xaa"), new GoogleDiffMatchPatch.Diff(DELETE, "a")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The xxx. The "), new GoogleDiffMatchPatch.Diff(INSERT, "zzz. The "), new GoogleDiffMatchPatch.Diff(EQUAL, "yyy."));
        dmp.diff_cleanupSemanticLossless(diffs);
        Assert.assertEquals("diff_cleanupSemanticLossless: Sentence boundaries.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The xxx."), new GoogleDiffMatchPatch.Diff(INSERT, " The zzz."), new GoogleDiffMatchPatch.Diff(EQUAL, " The yyy.")), diffs);
    }

    @Test
    public void testDiffCleanupSemantic() {
        // Cleanup semantically trivial equalities.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList();
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Null case.", diffList(), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "cd"), new GoogleDiffMatchPatch.Diff(EQUAL, "12"), new GoogleDiffMatchPatch.Diff(DELETE, "e"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: No elimination #1.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "cd"), new GoogleDiffMatchPatch.Diff(EQUAL, "12"), new GoogleDiffMatchPatch.Diff(DELETE, "e")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(INSERT, "ABC"), new GoogleDiffMatchPatch.Diff(EQUAL, "1234"), new GoogleDiffMatchPatch.Diff(DELETE, "wxyz"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: No elimination #2.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(INSERT, "ABC"), new GoogleDiffMatchPatch.Diff(EQUAL, "1234"), new GoogleDiffMatchPatch.Diff(DELETE, "wxyz")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "b"), new GoogleDiffMatchPatch.Diff(DELETE, "c"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Simple elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(INSERT, "b")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(EQUAL, "cd"), new GoogleDiffMatchPatch.Diff(DELETE, "e"), new GoogleDiffMatchPatch.Diff(EQUAL, "f"), new GoogleDiffMatchPatch.Diff(INSERT, "g"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Backpass elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcdef"), new GoogleDiffMatchPatch.Diff(INSERT, "cdfg")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, "1"), new GoogleDiffMatchPatch.Diff(EQUAL, "A"), new GoogleDiffMatchPatch.Diff(DELETE, "B"), new GoogleDiffMatchPatch.Diff(INSERT, "2"), new GoogleDiffMatchPatch.Diff(EQUAL, "_"), new GoogleDiffMatchPatch.Diff(INSERT, "1"), new GoogleDiffMatchPatch.Diff(EQUAL, "A"), new GoogleDiffMatchPatch.Diff(DELETE, "B"), new GoogleDiffMatchPatch.Diff(INSERT, "2"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Multiple elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "AB_AB"), new GoogleDiffMatchPatch.Diff(INSERT, "1A2_1A2")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The c"), new GoogleDiffMatchPatch.Diff(DELETE, "ow and the c"), new GoogleDiffMatchPatch.Diff(EQUAL, "at."));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Word boundaries.", diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "The "), new GoogleDiffMatchPatch.Diff(DELETE, "cow and the "), new GoogleDiffMatchPatch.Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcxx"), new GoogleDiffMatchPatch.Diff(INSERT, "xxdef"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: No overlap elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcxx"), new GoogleDiffMatchPatch.Diff(INSERT, "xxdef")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcxxx"), new GoogleDiffMatchPatch.Diff(INSERT, "xxxdef"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Overlap elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(EQUAL, "xxx"), new GoogleDiffMatchPatch.Diff(INSERT, "def")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "xxxabc"), new GoogleDiffMatchPatch.Diff(INSERT, "defxxx"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Reverse overlap elimination.", diffList(new GoogleDiffMatchPatch.Diff(INSERT, "def"), new GoogleDiffMatchPatch.Diff(EQUAL, "xxx"), new GoogleDiffMatchPatch.Diff(DELETE, "abc")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcd1212"), new GoogleDiffMatchPatch.Diff(INSERT, "1212efghi"), new GoogleDiffMatchPatch.Diff(EQUAL, "----"), new GoogleDiffMatchPatch.Diff(DELETE, "A3"), new GoogleDiffMatchPatch.Diff(INSERT, "3BC"));
        dmp.diff_cleanupSemantic(diffs);
        Assert.assertEquals("diff_cleanupSemantic: Two overlap eliminations.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abcd"), new GoogleDiffMatchPatch.Diff(EQUAL, "1212"), new GoogleDiffMatchPatch.Diff(INSERT, "efghi"), new GoogleDiffMatchPatch.Diff(EQUAL, "----"), new GoogleDiffMatchPatch.Diff(DELETE, "A"), new GoogleDiffMatchPatch.Diff(EQUAL, "3"), new GoogleDiffMatchPatch.Diff(INSERT, "BC")), diffs);
    }

    @Test
    public void testDiffCleanupEfficiency() {
        // Cleanup operationally trivial equalities.
        dmp.Diff_EditCost = 4;
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList();
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: Null case.", diffList(), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "wxyz"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "34"));
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: No elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "wxyz"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "34")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "34"));
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: Four-edit elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abxyzcd"), new GoogleDiffMatchPatch.Diff(INSERT, "12xyz34")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "x"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "34"));
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: Three-edit elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "xcd"), new GoogleDiffMatchPatch.Diff(INSERT, "12x34")), diffs);

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "xy"), new GoogleDiffMatchPatch.Diff(INSERT, "34"), new GoogleDiffMatchPatch.Diff(EQUAL, "z"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "56"));
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: Backpass elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abxyzcd"), new GoogleDiffMatchPatch.Diff(INSERT, "12xy34z56")), diffs);

        dmp.Diff_EditCost = 5;
        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "12"), new GoogleDiffMatchPatch.Diff(EQUAL, "wxyz"), new GoogleDiffMatchPatch.Diff(DELETE, "cd"), new GoogleDiffMatchPatch.Diff(INSERT, "34"));
        dmp.diff_cleanupEfficiency(diffs);
        Assert.assertEquals("diff_cleanupEfficiency: High cost elimination.", diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abwxyzcd"), new GoogleDiffMatchPatch.Diff(INSERT, "12wxyz34")), diffs);
        dmp.Diff_EditCost = 4;
    }

    @Test
    public void testDiffPrettyHtml() {
        // Pretty print.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a\n"), new GoogleDiffMatchPatch.Diff(DELETE, "<B>b</B>"), new GoogleDiffMatchPatch.Diff(INSERT, "c&d"));
        Assert.assertEquals("diff_prettyHtml:", "<span>a&para;<br></span><del style=\"background:#ffe6e6;\">&lt;B&gt;b&lt;/B&gt;</del><ins style=\"background:#e6ffe6;\">c&amp;d</ins>", dmp.diff_prettyHtml(diffs));
    }

    @Test
    public void testDiffText() {
        // Compute the source and destination texts.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "jump"), new GoogleDiffMatchPatch.Diff(DELETE, "s"), new GoogleDiffMatchPatch.Diff(INSERT, "ed"), new GoogleDiffMatchPatch.Diff(EQUAL, " over "), new GoogleDiffMatchPatch.Diff(DELETE, "the"), new GoogleDiffMatchPatch.Diff(INSERT, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, " lazy"));
        Assert.assertEquals("diff_text1:", "jumps over the lazy", dmp.diff_text1(diffs));
        Assert.assertEquals("diff_text2:", "jumped over a lazy", dmp.diff_text2(diffs));
    }

    @Test
    public void testDiffDelta() {
        // Convert a diff into delta string.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "jump"), new GoogleDiffMatchPatch.Diff(DELETE, "s"), new GoogleDiffMatchPatch.Diff(INSERT, "ed"), new GoogleDiffMatchPatch.Diff(EQUAL, " over "), new GoogleDiffMatchPatch.Diff(DELETE, "the"), new GoogleDiffMatchPatch.Diff(INSERT, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, " lazy"), new GoogleDiffMatchPatch.Diff(INSERT, "old dog"));
        String text1 = dmp.diff_text1(diffs);
        Assert.assertEquals("diff_text1: Base text.", "jumps over the lazy", text1);

        String delta = dmp.diff_toDelta(diffs);
        Assert.assertEquals("diff_toDelta:", "=4\t-1\t+ed\t=6\t-3\t+a\t=5\t+old dog", delta);

        // Convert delta string into a diff.
        Assert.assertEquals("diff_fromDelta: Normal.", diffs, dmp.diff_fromDelta(text1, delta));

        // Generates error (19 < 20).
        try {
            dmp.diff_fromDelta(text1 + "x", delta);
            Assert.fail("diff_fromDelta: Too long.");
        } catch (IllegalArgumentException ex) {
            // Exception expected.
        }

        // Generates error (19 > 18).
        try {
            dmp.diff_fromDelta(text1.substring(1), delta);
            Assert.fail("diff_fromDelta: Too short.");
        } catch (IllegalArgumentException ex) {
            // Exception expected.
        }

        // Generates error (%c3%xy invalid Unicode).
        try {
            dmp.diff_fromDelta("", "+%c3%xy");
            Assert.fail("diff_fromDelta: Invalid character.");
        } catch (IllegalArgumentException ex) {
            // Exception expected.
        }

        // Test deltas with special characters.
        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "\u0680 \000 \t %"), new GoogleDiffMatchPatch.Diff(DELETE, "\u0681 \001 \n ^"), new GoogleDiffMatchPatch.Diff(INSERT, "\u0682 \002 \\ |"));
        text1 = dmp.diff_text1(diffs);
        Assert.assertEquals("diff_text1: Unicode text.", "\u0680 \000 \t %\u0681 \001 \n ^", text1);

        delta = dmp.diff_toDelta(diffs);
        Assert.assertEquals("diff_toDelta: Unicode.", "=7\t-7\t+%DA%82 %02 %5C %7C", delta);

        Assert.assertEquals("diff_fromDelta: Unicode.", diffs, dmp.diff_fromDelta(text1, delta));

        // Verify pool of unchanged characters.
        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, "A-Z a-z 0-9 - _ . ! ~ * ' ( ) ; / ? : @ & = + $ , # "));
        String text2 = dmp.diff_text2(diffs);
        Assert.assertEquals("diff_text2: Unchanged characters.", "A-Z a-z 0-9 - _ . ! ~ * \' ( ) ; / ? : @ & = + $ , # ", text2);

        delta = dmp.diff_toDelta(diffs);
        Assert.assertEquals("diff_toDelta: Unchanged characters.", "+A-Z a-z 0-9 - _ . ! ~ * \' ( ) ; / ? : @ & = + $ , # ", delta);

        // Convert delta string into a diff.
        Assert.assertEquals("diff_fromDelta: Unchanged characters.", diffs, dmp.diff_fromDelta("", delta));
    }

    @Test
    public void testDiffXIndex() {
        // Translate a location in text1 to text2.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "1234"), new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"));
        Assert.assertEquals("diff_xIndex: Translation on equality.", 5, dmp.diff_xIndex(diffs, 2));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "1234"), new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"));
        Assert.assertEquals("diff_xIndex: Translation on deletion.", 1, dmp.diff_xIndex(diffs, 3));
    }

    @Test
    public void testDiffLevenshtein() {
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(INSERT, "1234"), new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"));
        Assert.assertEquals("Levenshtein with trailing equality.", 4, dmp.diff_levenshtein(diffs));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"), new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(INSERT, "1234"));
        Assert.assertEquals("Levenshtein with leading equality.", 4, dmp.diff_levenshtein(diffs));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "abc"), new GoogleDiffMatchPatch.Diff(EQUAL, "xyz"), new GoogleDiffMatchPatch.Diff(INSERT, "1234"));
        Assert.assertEquals("Levenshtein with middle equality.", 7, dmp.diff_levenshtein(diffs));
    }

//    public void testDiffBisect() {
//        // Normal.
//        String a = "cat";
//        String b = "map";
//        // Since the resulting diff hasn't been normalized, it would be ok if
//        // the insertion and deletion pairs are swapped.
//        // If the order changes, tweak this test as required.
//        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "c"), new GoogleDiffMatchPatch.Diff(INSERT, "m"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "t"), new GoogleDiffMatchPatch.Diff(INSERT, "p"));
//        assertEquals("diff_bisect: Normal.", diffs, dmp.diff_bisect(a, b, Long.MAX_VALUE));
//
//        // Timeout.
//        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "cat"), new GoogleDiffMatchPatch.Diff(INSERT, "map"));
//        assertEquals("diff_bisect: Timeout.", diffs, dmp.diff_bisect(a, b, 0));
//    }

    @Test
    public void testDiffMain() {
        // Perform a trivial diff.
        LinkedList<GoogleDiffMatchPatch.Diff> diffs = diffList();
        Assert.assertEquals("diff_main: Null case.", diffs, dmp.diff_main("", "", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "abc"));
        Assert.assertEquals("diff_main: Equality.", diffs, dmp.diff_main("abc", "abc", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "ab"), new GoogleDiffMatchPatch.Diff(INSERT, "123"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"));
        Assert.assertEquals("diff_main: Simple insertion.", diffs, dmp.diff_main("abc", "ab123c", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "123"), new GoogleDiffMatchPatch.Diff(EQUAL, "bc"));
        Assert.assertEquals("diff_main: Simple deletion.", diffs, dmp.diff_main("a123bc", "abc", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "123"), new GoogleDiffMatchPatch.Diff(EQUAL, "b"), new GoogleDiffMatchPatch.Diff(INSERT, "456"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"));
        Assert.assertEquals("diff_main: Two insertions.", diffs, dmp.diff_main("abc", "a123b456c", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "123"), new GoogleDiffMatchPatch.Diff(EQUAL, "b"), new GoogleDiffMatchPatch.Diff(DELETE, "456"), new GoogleDiffMatchPatch.Diff(EQUAL, "c"));
        Assert.assertEquals("diff_main: Two deletions.", diffs, dmp.diff_main("a123b456c", "abc", false));

        // Perform a real diff.
        // Switch off the timeout.
        dmp.Diff_Timeout = 0;
        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "b"));
        Assert.assertEquals("diff_main: Simple case #1.", diffs, dmp.diff_main("a", "b", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "Apple"), new GoogleDiffMatchPatch.Diff(INSERT, "Banana"), new GoogleDiffMatchPatch.Diff(EQUAL, "s are a"), new GoogleDiffMatchPatch.Diff(INSERT, "lso"), new GoogleDiffMatchPatch.Diff(EQUAL, " fruit."));
        Assert.assertEquals("diff_main: Simple case #2.", diffs, dmp.diff_main("Apples are a fruit.", "Bananas are also fruit.", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "\u0680"), new GoogleDiffMatchPatch.Diff(EQUAL, "x"), new GoogleDiffMatchPatch.Diff(DELETE, "\t"), new GoogleDiffMatchPatch.Diff(INSERT, "\000"));
        Assert.assertEquals("diff_main: Simple case #3.", diffs, dmp.diff_main("ax\t", "\u0680x\000", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "1"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "y"), new GoogleDiffMatchPatch.Diff(EQUAL, "b"), new GoogleDiffMatchPatch.Diff(DELETE, "2"), new GoogleDiffMatchPatch.Diff(INSERT, "xab"));
        Assert.assertEquals("diff_main: Overlap #1.", diffs, dmp.diff_main("1ayb2", "abxab", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, "xaxcx"), new GoogleDiffMatchPatch.Diff(EQUAL, "abc"), new GoogleDiffMatchPatch.Diff(DELETE, "y"));
        Assert.assertEquals("diff_main: Overlap #2.", diffs, dmp.diff_main("abcy", "xaxcxabc", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "ABCD"), new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(DELETE, "="), new GoogleDiffMatchPatch.Diff(INSERT, "-"), new GoogleDiffMatchPatch.Diff(EQUAL, "bcd"), new GoogleDiffMatchPatch.Diff(DELETE, "="), new GoogleDiffMatchPatch.Diff(INSERT, "-"), new GoogleDiffMatchPatch.Diff(EQUAL, "efghijklmnopqrs"), new GoogleDiffMatchPatch.Diff(DELETE, "EFGHIJKLMNOefg"));
        Assert.assertEquals("diff_main: Overlap #3.", diffs, dmp.diff_main("ABCDa=bcd=efghijklmnopqrsEFGHIJKLMNOefg", "a-bcd-efghijklmnopqrs", false));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(INSERT, " "), new GoogleDiffMatchPatch.Diff(EQUAL, "a"), new GoogleDiffMatchPatch.Diff(INSERT, "nd"), new GoogleDiffMatchPatch.Diff(EQUAL, " [[Pennsylvania]]"), new GoogleDiffMatchPatch.Diff(DELETE, " and [[New"));
        Assert.assertEquals("diff_main: Large equality.", diffs, dmp.diff_main("a [[Pennsylvania]] and [[New", " and [[Pennsylvania]]", false));

        dmp.Diff_Timeout = 0.1f;  // 100ms
        String a = "`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.\n";
        String b = "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.\n";
        // Increase the text lengths by 1024 times to ensure a timeout.
        for (int x = 0; x < 10; x++) {
            a = a + a;
            b = b + b;
        }
        long startTime = System.currentTimeMillis();
        dmp.diff_main(a, b);
        long endTime = System.currentTimeMillis();
        // Test that we took at least the timeout period.
        Assert.assertTrue("diff_main: Timeout min.", dmp.Diff_Timeout * 1000 <= endTime - startTime);
        // Test that we didn't take forever (be forgiving).
        // Theoretically this test could fail very occasionally if the
        // OS task swaps or locks up for a second at the wrong moment.
        Assert.assertTrue("diff_main: Timeout max.", dmp.Diff_Timeout * 1000 * 2 > endTime - startTime);
        dmp.Diff_Timeout = 0;

        // Test the linemode speedup.
        // Must be long to pass the 100 char cutoff.
        a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
        b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n";
        Assert.assertEquals("diff_main: Simple line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

        a = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        b = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij";
        Assert.assertEquals("diff_main: Single line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

        a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
        b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n";
        String[] texts_linemode = diff_rebuildtexts(dmp.diff_main(a, b, true));
        String[] texts_textmode = diff_rebuildtexts(dmp.diff_main(a, b, false));
        assertArrayEquals("diff_main: Overlap line-mode.", texts_textmode, texts_linemode);

        // Test null inputs.
        try {
            dmp.diff_main(null, null);
            Assert.fail("diff_main: Null inputs.");
        } catch (IllegalArgumentException ex) {
            // Error expected.
        }
    }


    //  MATCH TEST FUNCTIONS


//    public void testMatchAlphabet() {
//        // Initialise the bitmasks for Bitap.
//        Map<Character, Integer> bitmask;
//        bitmask = new HashMap<Character, Integer>();
//        bitmask.put('a', 4); bitmask.put('b', 2); bitmask.put('c', 1);
//        assertEquals("match_alphabet: Unique.", bitmask, dmp.match_alphabet("abc"));
//
//        bitmask = new HashMap<Character, Integer>();
//        bitmask.put('a', 37); bitmask.put('b', 18); bitmask.put('c', 8);
//        assertEquals("match_alphabet: Duplicates.", bitmask, dmp.match_alphabet("abcaba"));
//    }

//    public void testMatchBitap() {
//        // Bitap algorithm.
//        dmp.Match_Distance = 100;
//        dmp.Match_Threshold = 0.5f;
//        assertEquals("match_bitap: Exact match #1.", 5, dmp.match_bitap("abcdefghijk", "fgh", 5));
//
//        assertEquals("match_bitap: Exact match #2.", 5, dmp.match_bitap("abcdefghijk", "fgh", 0));
//
//        assertEquals("match_bitap: Fuzzy match #1.", 4, dmp.match_bitap("abcdefghijk", "efxhi", 0));
//
//        assertEquals("match_bitap: Fuzzy match #2.", 2, dmp.match_bitap("abcdefghijk", "cdefxyhijk", 5));
//
//        assertEquals("match_bitap: Fuzzy match #3.", -1, dmp.match_bitap("abcdefghijk", "bxy", 1));
//
//        assertEquals("match_bitap: Overflow.", 2, dmp.match_bitap("123456789xx0", "3456789x0", 2));
//
//        assertEquals("match_bitap: Before start match.", 0, dmp.match_bitap("abcdef", "xxabc", 4));
//
//        assertEquals("match_bitap: Beyond end match.", 3, dmp.match_bitap("abcdef", "defyy", 4));
//
//        assertEquals("match_bitap: Oversized pattern.", 0, dmp.match_bitap("abcdef", "xabcdefy", 0));
//
//        dmp.Match_Threshold = 0.4f;
//        assertEquals("match_bitap: Threshold #1.", 4, dmp.match_bitap("abcdefghijk", "efxyhi", 1));
//
//        dmp.Match_Threshold = 0.3f;
//        assertEquals("match_bitap: Threshold #2.", -1, dmp.match_bitap("abcdefghijk", "efxyhi", 1));
//
//        dmp.Match_Threshold = 0.0f;
//        assertEquals("match_bitap: Threshold #3.", 1, dmp.match_bitap("abcdefghijk", "bcdef", 1));
//
//        dmp.Match_Threshold = 0.5f;
//        assertEquals("match_bitap: Multiple select #1.", 0, dmp.match_bitap("abcdexyzabcde", "abccde", 3));
//
//        assertEquals("match_bitap: Multiple select #2.", 8, dmp.match_bitap("abcdexyzabcde", "abccde", 5));
//
//        dmp.Match_Distance = 10;  // Strict location.
//        assertEquals("match_bitap: Distance test #1.", -1, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24));
//
//        assertEquals("match_bitap: Distance test #2.", 0, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcdxxefg", 1));
//
//        dmp.Match_Distance = 1000;  // Loose location.
//        assertEquals("match_bitap: Distance test #3.", 0, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24));
//    }

    @Test
    public void testMatchMain() {
        // Full match.
        Assert.assertEquals("match_main: Equality.", 0, dmp.match_main("abcdef", "abcdef", 1000));

        Assert.assertEquals("match_main: Null text.", -1, dmp.match_main("", "abcdef", 1));

        Assert.assertEquals("match_main: Null pattern.", 3, dmp.match_main("abcdef", "", 3));

        Assert.assertEquals("match_main: Exact match.", 3, dmp.match_main("abcdef", "de", 3));

        Assert.assertEquals("match_main: Beyond end match.", 3, dmp.match_main("abcdef", "defy", 4));

        Assert.assertEquals("match_main: Oversized pattern.", 0, dmp.match_main("abcdef", "abcdefy", 0));

        dmp.Match_Threshold = 0.7f;
        Assert.assertEquals("match_main: Complex match.", 4, dmp.match_main("I am the very model of a modern major general.", " that berry ", 5));
        dmp.Match_Threshold = 0.5f;

        // Test null inputs.
        try {
            dmp.match_main(null, null, 0);
            Assert.fail("match_main: Null inputs.");
        } catch (IllegalArgumentException ex) {
            // Error expected.
        }
    }


    //  PATCH TEST FUNCTIONS


    @Test
    public void testPatchObj() {
        // Patch Object.
        GoogleDiffMatchPatch.Patch p = new GoogleDiffMatchPatch.Patch();
        p.start1 = 20;
        p.start2 = 21;
        p.length1 = 18;
        p.length2 = 17;
        p.diffs = diffList(new GoogleDiffMatchPatch.Diff(EQUAL, "jump"), new GoogleDiffMatchPatch.Diff(DELETE, "s"), new GoogleDiffMatchPatch.Diff(INSERT, "ed"), new GoogleDiffMatchPatch.Diff(EQUAL, " over "), new GoogleDiffMatchPatch.Diff(DELETE, "the"), new GoogleDiffMatchPatch.Diff(INSERT, "a"), new GoogleDiffMatchPatch.Diff(EQUAL, "\nlaz"));
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n %0Alaz\n";
        Assert.assertEquals("Patch: toString.", strp, p.toString());
    }

    @Test
    public void testPatchFromText() {
        Assert.assertTrue("patch_fromText: #0.", dmp.patch_fromText("").isEmpty());

        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n %0Alaz\n";
        Assert.assertEquals("patch_fromText: #1.", strp, dmp.patch_fromText(strp).get(0).toString());

        Assert.assertEquals("patch_fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", dmp.patch_fromText("@@ -1 +1 @@\n-a\n+b\n").get(0).toString());

        Assert.assertEquals("patch_fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", dmp.patch_fromText("@@ -1,3 +0,0 @@\n-abc\n").get(0).toString());

        Assert.assertEquals("patch_fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", dmp.patch_fromText("@@ -0,0 +1,3 @@\n+abc\n").get(0).toString());

        // Generates error.
        try {
            dmp.patch_fromText("Bad\nPatch\n");
            Assert.fail("patch_fromText: #5.");
        } catch (IllegalArgumentException ex) {
            // Exception expected.
        }
    }

    @Test
    public void testPatchToText() {
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
        List<GoogleDiffMatchPatch.Patch> patches;
        patches = dmp.patch_fromText(strp);
        Assert.assertEquals("patch_toText: Single.", strp, dmp.patch_toText(patches));

        strp = "@@ -1,9 +1,9 @@\n-f\n+F\n oo+fooba\n@@ -7,9 +7,9 @@\n obar\n-,\n+.\n  tes\n";
        patches = dmp.patch_fromText(strp);
        Assert.assertEquals("patch_toText: Dual.", strp, dmp.patch_toText(patches));
    }

//    public void testPatchAddContext() {
//        dmp.Patch_Margin = 4;
//        GoogleDiffMatchPatch.Patch p;
//        p = dmp.patch_fromText("@@ -21,4 +21,10 @@\n-jump\n+somersault\n").get(0);
//        dmp.patch_addContext(p, "The quick brown fox jumps over the lazy dog.");
//        assertEquals("patch_addContext: Simple case.", "@@ -17,12 +17,18 @@\n fox \n-jump\n+somersault\n s ov\n", p.toString());
//
//        p = dmp.patch_fromText("@@ -21,4 +21,10 @@\n-jump\n+somersault\n").get(0);
//        dmp.patch_addContext(p, "The quick brown fox jumps.");
//        assertEquals("patch_addContext: Not enough trailing context.", "@@ -17,10 +17,16 @@\n fox \n-jump\n+somersault\n s.\n", p.toString());
//
//        p = dmp.patch_fromText("@@ -3 +3,2 @@\n-e\n+at\n").get(0);
//        dmp.patch_addContext(p, "The quick brown fox jumps.");
//        assertEquals("patch_addContext: Not enough leading context.", "@@ -1,7 +1,8 @@\n Th\n-e\n+at\n  qui\n", p.toString());
//
//        p = dmp.patch_fromText("@@ -3 +3,2 @@\n-e\n+at\n").get(0);
//        dmp.patch_addContext(p, "The quick brown fox jumps.  The quick brown fox crashes.");
//        assertEquals("patch_addContext: Ambiguity.", "@@ -1,27 +1,28 @@\n Th\n-e\n+at\n  quick brown fox jumps. \n", p.toString());
//    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPatchMake() {
        LinkedList<GoogleDiffMatchPatch.Patch> patches;
        patches = dmp.patch_make("", "");
        Assert.assertEquals("patch_make: Null case.", "", dmp.patch_toText(patches));

        String text1 = "The quick brown fox jumps over the lazy dog.";
        String text2 = "That quick brown fox jumped over a lazy dog.";
        String expectedPatch = "@@ -1,8 +1,7 @@\n Th\n-at\n+e\n  qui\n@@ -21,17 +21,18 @@\n jump\n-ed\n+s\n  over \n-a\n+the\n  laz\n";
        // The second patch must be "-21,17 +21,18", not "-22,17 +21,18" due to rolling context.
        patches = dmp.patch_make(text2, text1);
        Assert.assertEquals("patch_make: Text2+Text1 inputs.", expectedPatch, dmp.patch_toText(patches));

        expectedPatch = "@@ -1,11 +1,12 @@\n Th\n-e\n+at\n  quick b\n@@ -22,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
        patches = dmp.patch_make(text1, text2);
        Assert.assertEquals("patch_make: Text1+Text2 inputs.", expectedPatch, dmp.patch_toText(patches));

        LinkedList<GoogleDiffMatchPatch.Diff> diffs = dmp.diff_main(text1, text2, false);
        patches = dmp.patch_make(diffs);
        Assert.assertEquals("patch_make: Diff input.", expectedPatch, dmp.patch_toText(patches));

        patches = dmp.patch_make(text1, diffs);
        Assert.assertEquals("patch_make: Text1+Diff inputs.", expectedPatch, dmp.patch_toText(patches));

        patches = dmp.patch_make(text1, text2, diffs);
        Assert.assertEquals("patch_make: Text1+Text2+Diff inputs (deprecated).", expectedPatch, dmp.patch_toText(patches));

        patches = dmp.patch_make("`1234567890-=[]\\;',./", "~!@#$%^&*()_+{}|:\"<>?");
        Assert.assertEquals("patch_toText: Character encoding.", "@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n", dmp.patch_toText(patches));

        diffs = diffList(new GoogleDiffMatchPatch.Diff(DELETE, "`1234567890-=[]\\;',./"), new GoogleDiffMatchPatch.Diff(INSERT, "~!@#$%^&*()_+{}|:\"<>?"));
        Assert.assertEquals("patch_fromText: Character decoding.", diffs, dmp.patch_fromText("@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n").get(0).diffs);

        text1 = "";
        for (int x = 0; x < 100; x++) {
            text1 += "abcdef";
        }
        text2 = text1 + "123";
        expectedPatch = "@@ -573,28 +573,31 @@\n cdefabcdefabcdefabcdefabcdef\n+123\n";
        patches = dmp.patch_make(text1, text2);
        Assert.assertEquals("patch_make: Long string with repeats.", expectedPatch, dmp.patch_toText(patches));

        // Test null inputs.
        try {
            dmp.patch_make(null);
            Assert.fail("patch_make: Null inputs.");
        } catch (IllegalArgumentException ex) {
            // Error expected.
        }
    }

    @Test
    public void testPatchSplitMax() {
        // Assumes that Match_MaxBits is 32.
        LinkedList<GoogleDiffMatchPatch.Patch> patches;
        patches = dmp.patch_make("abcdefghijklmnopqrstuvwxyz01234567890", "XabXcdXefXghXijXklXmnXopXqrXstXuvXwxXyzX01X23X45X67X89X0");
        dmp.patch_splitMax(patches);
        Assert.assertEquals("patch_splitMax: #1.", "@@ -1,32 +1,46 @@\n+X\n ab\n+X\n cd\n+X\n ef\n+X\n gh\n+X\n ij\n+X\n kl\n+X\n mn\n+X\n op\n+X\n qr\n+X\n st\n+X\n uv\n+X\n wx\n+X\n yz\n+X\n 012345\n@@ -25,13 +39,18 @@\n zX01\n+X\n 23\n+X\n 45\n+X\n 67\n+X\n 89\n+X\n 0\n", dmp.patch_toText(patches));

        patches = dmp.patch_make("abcdef1234567890123456789012345678901234567890123456789012345678901234567890uvwxyz", "abcdefuvwxyz");
        String oldToText = dmp.patch_toText(patches);
        dmp.patch_splitMax(patches);
        Assert.assertEquals("patch_splitMax: #2.", oldToText, dmp.patch_toText(patches));

        patches = dmp.patch_make("1234567890123456789012345678901234567890123456789012345678901234567890", "abc");
        dmp.patch_splitMax(patches);
        Assert.assertEquals("patch_splitMax: #3.", "@@ -1,32 +1,4 @@\n-1234567890123456789012345678\n 9012\n@@ -29,32 +1,4 @@\n-9012345678901234567890123456\n 7890\n@@ -57,14 +1,3 @@\n-78901234567890\n+abc\n", dmp.patch_toText(patches));

        patches = dmp.patch_make("abcdefghij , h : 0 , t : 1 abcdefghij , h : 0 , t : 1 abcdefghij , h : 0 , t : 1", "abcdefghij , h : 1 , t : 1 abcdefghij , h : 1 , t : 1 abcdefghij , h : 0 , t : 1");
        dmp.patch_splitMax(patches);
        Assert.assertEquals("patch_splitMax: #4.", "@@ -2,32 +2,32 @@\n bcdefghij , h : \n-0\n+1\n  , t : 1 abcdef\n@@ -29,32 +29,32 @@\n bcdefghij , h : \n-0\n+1\n  , t : 1 abcdef\n", dmp.patch_toText(patches));
    }

    @Test
    public void testPatchAddPadding() {
        LinkedList<GoogleDiffMatchPatch.Patch> patches;
        patches = dmp.patch_make("", "test");
        Assert.assertEquals("patch_addPadding: Both edges full.", "@@ -0,0 +1,4 @@\n+test\n", dmp.patch_toText(patches));
        dmp.patch_addPadding(patches);
        Assert.assertEquals("patch_addPadding: Both edges full.", "@@ -1,8 +1,12 @@\n %01%02%03%04\n+test\n %01%02%03%04\n", dmp.patch_toText(patches));

        patches = dmp.patch_make("XY", "XtestY");
        Assert.assertEquals("patch_addPadding: Both edges partial.", "@@ -1,2 +1,6 @@\n X\n+test\n Y\n", dmp.patch_toText(patches));
        dmp.patch_addPadding(patches);
        Assert.assertEquals("patch_addPadding: Both edges partial.", "@@ -2,8 +2,12 @@\n %02%03%04X\n+test\n Y%01%02%03\n", dmp.patch_toText(patches));

        patches = dmp.patch_make("XXXXYYYY", "XXXXtestYYYY");
        Assert.assertEquals("patch_addPadding: Both edges none.", "@@ -1,8 +1,12 @@\n XXXX\n+test\n YYYY\n", dmp.patch_toText(patches));
        dmp.patch_addPadding(patches);
        Assert.assertEquals("patch_addPadding: Both edges none.", "@@ -5,8 +5,12 @@\n XXXX\n+test\n YYYY\n", dmp.patch_toText(patches));
    }

    @Test
    public void testPatchApply() {
        dmp.Match_Distance = 1000;
        dmp.Match_Threshold = 0.5f;
        dmp.Patch_DeleteThreshold = 0.5f;
        LinkedList<GoogleDiffMatchPatch.Patch> patches;
        patches = dmp.patch_make("", "");
        Object[] results = dmp.patch_apply(patches, "Hello world.");
        boolean[] boolArray = (boolean[]) results[1];
        String resultStr = results[0] + "\t" + boolArray.length;
        Assert.assertEquals("patch_apply: Null case.", "Hello world.\t0", resultStr);

        patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog.");
        results = dmp.patch_apply(patches, "The quick brown fox jumps over the lazy dog.");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Exact match.", "That quick brown fox jumped over a lazy dog.\ttrue\ttrue", resultStr);

        results = dmp.patch_apply(patches, "The quick red rabbit jumps over the tired tiger.");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Partial match.", "That quick red rabbit jumped over a tired tiger.\ttrue\ttrue", resultStr);

        results = dmp.patch_apply(patches, "I am the very model of a modern major general.");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Failed match.", "I am the very model of a modern major general.\tfalse\tfalse", resultStr);

        patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
        results = dmp.patch_apply(patches, "x123456789012345678901234567890-----++++++++++-----123456789012345678901234567890y");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Big delete, small change.", "xabcy\ttrue\ttrue", resultStr);

        patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
        results = dmp.patch_apply(patches, "x12345678901234567890---------------++++++++++---------------12345678901234567890y");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Big delete, big change 1.", "xabc12345678901234567890---------------++++++++++---------------12345678901234567890y\tfalse\ttrue", resultStr);

        dmp.Patch_DeleteThreshold = 0.6f;
        patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
        results = dmp.patch_apply(patches, "x12345678901234567890---------------++++++++++---------------12345678901234567890y");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Big delete, big change 2.", "xabcy\ttrue\ttrue", resultStr);
        dmp.Patch_DeleteThreshold = 0.5f;

        // Compensate for failed patch.
        dmp.Match_Threshold = 0.0f;
        dmp.Match_Distance = 0;
        patches = dmp.patch_make("abcdefghijklmnopqrstuvwxyz--------------------1234567890", "abcXXXXXXXXXXdefghijklmnopqrstuvwxyz--------------------1234567YYYYYYYYYY890");
        results = dmp.patch_apply(patches, "ABCDEFGHIJKLMNOPQRSTUVWXYZ--------------------1234567890");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
        Assert.assertEquals("patch_apply: Compensate for failed patch.", "ABCDEFGHIJKLMNOPQRSTUVWXYZ--------------------1234567YYYYYYYYYY890\tfalse\ttrue", resultStr);
        dmp.Match_Threshold = 0.5f;
        dmp.Match_Distance = 1000;

        patches = dmp.patch_make("", "test");
        String patchStr = dmp.patch_toText(patches);
        dmp.patch_apply(patches, "");
        Assert.assertEquals("patch_apply: No side effects.", patchStr, dmp.patch_toText(patches));

        patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "Woof");
        patchStr = dmp.patch_toText(patches);
        dmp.patch_apply(patches, "The quick brown fox jumps over the lazy dog.");
        Assert.assertEquals("patch_apply: No side effects with major delete.", patchStr, dmp.patch_toText(patches));

        patches = dmp.patch_make("", "test");
        results = dmp.patch_apply(patches, "");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0];
        Assert.assertEquals("patch_apply: Edge exact match.", "test\ttrue", resultStr);

        patches = dmp.patch_make("XY", "XtestY");
        results = dmp.patch_apply(patches, "XY");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0];
        Assert.assertEquals("patch_apply: Near edge exact match.", "XtestY\ttrue", resultStr);

        patches = dmp.patch_make("y", "y123");
        results = dmp.patch_apply(patches, "x");
        boolArray = (boolean[]) results[1];
        resultStr = results[0] + "\t" + boolArray[0];
        Assert.assertEquals("patch_apply: Edge partial match.", "x123\ttrue", resultStr);
    }

    private void assertArrayEquals(String error_msg, Object[] a, Object[] b) {
        List<Object> list_a = Arrays.asList(a);
        List<Object> list_b = Arrays.asList(b);
        Assert.assertEquals(error_msg, list_a, list_b);
    }

//    private void assertLinesToCharsResultEquals(String error_msg,
//                                                GoogleDiffMatchPatch.LinesToCharsResult a, GoogleDiffMatchPatch.LinesToCharsResult b) {
//        assertEquals(error_msg, a.chars1, b.chars1);
//        assertEquals(error_msg, a.chars2, b.chars2);
//        assertEquals(error_msg, a.lineArray, b.lineArray);
//    }

    // Construct the two texts which made up the diff originally.
    private static String[] diff_rebuildtexts(LinkedList<GoogleDiffMatchPatch.Diff> diffs) {
        String[] text = {"", ""};
        for (GoogleDiffMatchPatch.Diff myDiff : diffs) {
            if (myDiff.operation != GoogleDiffMatchPatch.Operation.INSERT) {
                text[0] += myDiff.text;
            }
            if (myDiff.operation != GoogleDiffMatchPatch.Operation.DELETE) {
                text[1] += myDiff.text;
            }
        }
        return text;
    }

    // Private function for quickly building lists of diffs.
    private static LinkedList<GoogleDiffMatchPatch.Diff> diffList(GoogleDiffMatchPatch.Diff... diffs) {
        LinkedList<GoogleDiffMatchPatch.Diff> myDiffList = new LinkedList<GoogleDiffMatchPatch.Diff>();
        for (GoogleDiffMatchPatch.Diff myDiff : diffs) {
            myDiffList.add(myDiff);
        }
        return myDiffList;
    }
}
