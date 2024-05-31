package cn.molokymc.prideplus.utils.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReleaseType {

    RELEASE("Release"),
    BETA("Beta"),
    DEV("Dev");

    private final String name;

}
