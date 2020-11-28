package net.thevpc.app.vainruling.core.service.export;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.thevpc.common.vfs.VFS;
import net.thevpc.common.vfs.VFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class VExcelWriter implements AutoCloseable {

    private VFile template;
    private VFile output;
    private File ff = null;
    private WritableWorkbook copy;
    private int initialCount;
    private int generatedCount;
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

            initialCount = copy.getSheets().length;
        }
    }

    public void removeInitialSheets() {
        while (initialCount > 0) {
            initialCount--;
            copy.removeSheet(0);
        }
    }

    public void generateExcelSheet(int sheetIndex, final Map<String, Object> dataSet) throws IOException {
        checkStarted();
        WritableSheet sheet = copy.getSheet(sheetIndex);
        ExcelTemplate.generateExcelSheet(sheet, dataSet);
    }

    public void generateNextExcelSheet(int sheetToCopy, String name, final Map<String, Object> dataSet) throws IOException {
        copySheet(0, name, getInitialCount() + generatedCount);
        generateExcelSheet(getInitialCount() +generatedCount, dataSet);
        generatedCount++;
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

    @Override
    public void close() {
        try {
            write();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public int getInitialCount() throws IOException {
        checkStarted();
        return initialCount;
    }

    public void copySheet(int sheetIndex, String name, int newIndex) {
        copy.copySheet(sheetIndex, name, newIndex);
    }

}
