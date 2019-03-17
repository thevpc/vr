package net.vpc.app.vainruling.plugins.academic.service.load;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.vpc.app.vainruling.core.service.util.ExcelTemplate;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class VExcelWriter implements AutoCloseable{
    private VFile template;
    private VFile output;
    private File ff = null;
    private WritableWorkbook copy;
    private int count0;
    private Workbook workbook;
    private boolean started = false;
    private boolean written = false;

    public VExcelWriter(VFile template, VFile output) {
        this.template = template;
        this.output = output;
    }

    public void checkStarted() throws IOException {
        if (!started) {
            started = true;
            InputStream in = null;
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
            final VFile p = output.getParentFile();
            if (p != null) {
                p.mkdirs();
            }
            ff = File.createTempFile("tmp", "tmp." + output.getFileName().getShortExtension());
            copy = Workbook.createWorkbook(ff, workbook);

            count0 = copy.getSheets().length;
        }
    }

    public void removeInitialSheets() {
        while (count0 > 0) {
            count0--;
            copy.removeSheet(0);
        }
    }

    public void generateExcelSheet(int sheetIndex, final Map<String, Object> dataSet) throws IOException {
        checkStarted();
        WritableSheet sheet = copy.getSheet(sheetIndex);
        ExcelTemplate.generateExcelSheet(sheet, dataSet);
    }


    public void write() throws IOException {
        if (started) {
            if (!written) {
                written = true;
                copy.write();
                workbook.close();
                try {
                    copy.close();
                } catch (WriteException e) {
                    throw new IOException(e);
                }
                VFS.createNativeFS().copyTo(ff.getPath(), output);
                if (ff != null) {
                    ff.delete();
                }
            }
        }
    }

    public void close() {
        try {
            write();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public int getInitialCount() throws IOException {
        checkStarted();
        return count0;
    }

    public void copySheet(int sheetIndex, String name, int newIndex) {
        copy.copySheet(sheetIndex, name, newIndex);
    }

}
