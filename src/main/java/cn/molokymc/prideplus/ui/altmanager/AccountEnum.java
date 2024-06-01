package cn.molokymc.prideplus.ui.altmanager;

public enum AccountEnum {
    OFFLINE("OFFLINE"),
    MICROSOFT("MICROSOFT");

    private final String writeName;

    private AccountEnum(String name) {
        this.writeName = name;
    }

    public static AccountEnum parse(String str) {
        for (AccountEnum value : AccountEnum.values()) {
            if (!value.writeName.equals(str)) continue;
            return value;
        }
        return null;
    }
}

