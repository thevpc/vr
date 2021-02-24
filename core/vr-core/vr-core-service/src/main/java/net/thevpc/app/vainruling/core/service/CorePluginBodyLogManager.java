package net.thevpc.app.vainruling.core.service;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.strings.StringUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

class CorePluginBodyLogManager extends CorePluginBody {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodyLogManager.class.getName());
    private final net.thevpc.common.collections.EvictingQueue<String> lines = Collections2.<String>evictingQueue(500);
    private LogTailer tailer;

    @Override
    public void onInstall() {

    }

    @Override
    public void onStart() {

    }

    public String getServerLog() {
        prepare();
        synchronized (lines) {
            return lines.stream().collect(Collectors.joining("\n"));
        }
    }

    private void prepare() {
        String serverLog = (String) getContext().getCorePlugin().getOrCreateAppPropertyValue("System.Server.Log", null, "");
        if (tailer != null) {
            if (Objects.equals(serverLog, tailer.serverLog)) {
                tailer.update();
                return;
            }
            tailer.stop();
            tailer = null;
        }
        if (!StringUtils.isBlank(serverLog)) {
            File file = new File(serverLog);
            if (file.exists()) {
                tailer = new LogTailer(serverLog);
            }
        }
    }

    private class LogTailer {

        private String serverLog;
        private Tailer tailer;
        private Thread thread;

        public LogTailer(String serverLog) {
            this.serverLog = serverLog;
            if (!StringUtils.isBlank(serverLog)) {
                File file = new File(serverLog);
                if (file.exists()) {
                    tailer = new Tailer(file, new TailerListener() {

                        @Override
                        public void init(Tailer tailer) {
                            synchronized (lines) {
                                lines.clear();
                            }
                        }

                        @Override
                        public void fileNotFound() {
                            synchronized (lines) {
                                lines.add("**EVENT** fileNotFound");
                            }
                        }

                        @Override
                        public void fileRotated() {
                            synchronized (lines) {
                                lines.add("**EVENT** fileRotated");
                            }
                        }

                        @Override
                        public void handle(String line) {
                            synchronized (lines) {
                                lines.add(line);
                            }
                        }

                        @Override
                        public void handle(Exception ex) {
                            synchronized (lines) {
                                lines.add("**EVENT** error:" + ex);
                            }
                        }
                    }, 1000, true);
                    thread = new Thread(tailer, "LogTailer");
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }

        public void update() {

        }

        public void stop() {
            if (tailer != null) {
                tailer.stop();
                tailer = null;
                thread = null;
            }
        }
    }

}
