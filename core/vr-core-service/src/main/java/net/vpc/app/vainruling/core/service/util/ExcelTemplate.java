/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.vpc.common.util.Convert;
import net.vpc.common.util.PlatformTypes;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 *
 * @author vpc
 */
public class ExcelTemplate {

    private static PropertyPlaceholderHelper hp = new PropertyPlaceholderHelper("${", "}");

    public static void generateExcel(VFile template, VFile output, Map<String, Object> dataSet) throws IOException {
        try {
            Workbook workbook;
            InputStream in = null;
            OutputStream out = null;
            try {
                try {
                    in = template.getInputStream();
                    workbook = Workbook.getWorkbook(in);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (BiffException ex) {
                throw new IOException(ex);
            }
            VFile parentFile = output.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            File ff = null;
            try {
                ff = File.createTempFile("tmp", ".xls");
                WritableWorkbook copy = Workbook.createWorkbook(ff, workbook);
                for (WritableSheet sheet : copy.getSheets()) {
                    generateExcelSheet(sheet, dataSet);
                }
                copy.write();
                workbook.close();
                copy.close();
                VFS.createNativeFS().copyTo(ff.getPath(), output);
            } finally {
                if (ff != null) {
                    ff.delete();
                }
//                if(out!=null){
//                    out.close();
//                }
            }
        } catch (WriteException ex) {
            throw new IOException(ex);
        }
    }

    public static void generateExcelSheet(WritableSheet sheet, final Map<String, Object> dataSet) throws IOException {
        int columns = sheet.getColumns();
        int rows = sheet.getRows();
        final Map<String, Prop> props = new HashMap<>();
        PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver = new PropertyPlaceholderHelper.PlaceholderResolver() {

            @Override
            public String resolvePlaceholder(String placeholderName) {
                String varExpr = placeholderName.trim();
                Prop pp = props.get(varExpr);
                if (pp == null) {
                    pp = new Prop(varExpr);
                    props.put(varExpr, pp);
                }
                return pp.eval(dataSet);
            }
        };

        if (sheet.getName().contains("${")) {
            String v2 = hp.replacePlaceholders(sheet.getName(), placeholderResolver);
            sheet.setName(v2);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                WritableCell cell = sheet.getWritableCell(c, r);
                if (cell.getContents().contains("${")) {
                    String v2 = hp.replacePlaceholders(cell.getContents(), placeholderResolver);
                    if (PlatformTypes.isDouble(v2.trim())) {
                        jxl.write.Number n = new jxl.write.Number(c, r, Double.parseDouble(v2.trim()), cell.getCellFormat());
                        try {
                            sheet.addCell(n);
                        } catch (WriteException ex) {
                            throw new IOException(ex);
                        }
                    } else {
                        if (cell.getType() == CellType.LABEL) {
                            Label l = (Label) cell;
                            l.setString(v2);
                        } else {
                            //why?
                        }
                    }
                }
            }
        }

    }

    private static PropertyPlaceholderHelper hv = new PropertyPlaceholderHelper("#", "#");

    private static class Prop {

        String name;
        List<String> varNames = new ArrayList<>();
        Map<String, Integer> varValues = new HashMap<>();
        PropertyPlaceholderHelper.PlaceholderResolver vres2 = new PropertyPlaceholderHelper.PlaceholderResolver() {

            @Override
            public String resolvePlaceholder(String varName0) {
                String varName = varName0.trim();
                Integer v = varValues.get(varName);
                if (v == null) {
                    v = 1;
                }
                varValues.put(varName, v);
                return String.valueOf(v);
            }
        };

        public Prop(String name) {
            this.name = name;
            PropertyPlaceholderHelper.PlaceholderResolver vres = new PropertyPlaceholderHelper.PlaceholderResolver() {

                @Override
                public String resolvePlaceholder(String varName0) {
                    String varName = varName0.trim();
                    varNames.add(varName);
                    Integer v = varValues.get(varName);
                    if (v == null) {
                        v = 1;
                    }
                    varValues.put(varName, v);
                    return String.valueOf(v);
                }
            };
            hv.replacePlaceholders(name, vres);
        }

        public String eval(Map<String, Object> properties) {
            String value = (varValues == null) ? name : eval0(varValues);
            if (properties.containsKey(value)) {
                String s = Convert.toNonNullString(properties.get(value));
                if (s == null) {
                    s = "";
                }
                if (varValues != null) {
                    Map<String, Integer> incs = inc(properties);
                    varValues = incs;
                }
                return s;
            }
            return "";
        }

        public Map<String, Integer> inc(Map<String, Object> properties) {
            Map<String, Integer> varValuesMap = new HashMap<>(varValues);
            int varPos = varNames.size() - 1;
            Map<String, Integer> ret = inc0(varValuesMap, varPos, properties);
//            System.out.println(name + ":" + varValuesMap + " => " + ret);
            return ret;
        }

        public Map<String, Integer> inc0(Map<String, Integer> varValuesMap, int varPos, Map<String, Object> properties) {
            if (varPos < 0) {
                return null;
            }
            Map<String, Integer> varValues2 = new HashMap<>(varValuesMap);
            String varName = varNames.get(varPos);
            int y = varValues2.get(varName);
            y++;
            varValues2.put(varName, y);
            String tt = eval0(varValues2);
            if (properties.containsKey(tt)) {
                return varValues2;
            } else {
                if (varPos <= 0) {
                    return null;
                }
                varValues2.put(varName, 0);
                return inc0(varValuesMap, varPos, properties);
            }
        }

        public String eval0(final Map<String, Integer> varValues0) {
            PropertyPlaceholderHelper.PlaceholderResolver vres = new PropertyPlaceholderHelper.PlaceholderResolver() {

                @Override
                public String resolvePlaceholder(String varName0) {
                    String varName = varName0.trim();
//                    varNames.add(varName);
                    Integer v = varValues0.get(varName);
                    if (v == null) {
                        v = 1;
                    }
                    varValues0.put(varName, v);
                    return String.valueOf(v);
                }
            };
            return hv.replacePlaceholders(name, vres);
        }

        public String getName() {
            return name;
        }

    }
}
