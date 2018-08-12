package net.vpc.app.vr.core.toolbox;

public class ValidatorFactory {

    public static StringValidator STRING = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
    };
    
    public static StringValidator NAME = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
        @Override
        public String getHints() {
            return "consider lower case '-' sepearated name, like my-name";
        }
    };
    
    public static StringValidator URL = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
        @Override
        public String getHints() {
            return "use http url";
        }
    };
    
    public static StringValidator LABEL = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
        @Override
        public String getHints() {
            return "consider capitalized ' ' sepearated name, like My Name";
        }
    };
    
    public static StringValidator VERSION = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
        @Override
        public String getHints() {
            return "use format : x.y.z";
        }
    };
    public static StringValidator FOLDER = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
    };
    public static StringValidator GROUP = new StringValidator() {
        @Override
        public String validate(String value) {
            return value;
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.STRING;
        }
        @Override
        public String getHints() {
            return "use package format : com.company.project";
        }
    };
    public static StringValidator BOOLEAN = new StringValidator() {
        @Override
        public String validate(String value) {
            if ("true".equalsIgnoreCase(value)
                    || "false".equalsIgnoreCase(value)
                    || "yes".equalsIgnoreCase(value)
                    || "no".equalsIgnoreCase(value)) {
                return "true";
            }
            throw new IllegalArgumentException("Expected  : yes, no, true, false");
        }

        @Override
        public String getHints() {
            return "Accepted values are yes, no, true, false";
        }

        @Override
        public StringValidatorType getType() {
            return StringValidatorType.BOOLEAN;
        }
    };
}
