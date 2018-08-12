package net.vpc.app.vr.core.toolbox;

public class ValidatorFactory {

    public static StringValidator STRING = new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
    public static StringValidator ID = new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
    public static StringValidator VERSION = new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
    public static StringValidator FOLDER = new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
    public static StringValidator GROUP = new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
    static StringValidator BOOLEAN = new StringValidator() {
        @Override
        public boolean validate(String value) {
            if ("true".equalsIgnoreCase(value)
                    || "false".equalsIgnoreCase(value)
                    || "yes".equalsIgnoreCase(value)
                    || "no".equalsIgnoreCase(value)) {
                return true;
            }
            throw new IllegalArgumentException("Expected  : yes, no, true, false");
        }
    };
}
