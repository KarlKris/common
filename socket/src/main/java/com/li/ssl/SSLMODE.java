package com.li.ssl;

/** ssl模式 **/
public enum SSLMODE {

    /** 单向 **/
    CA,

    /** 双向 **/
    CSA,

    ;

    public static boolean contain(String mode) {
        for (SSLMODE s : SSLMODE.values()) {
            if (s.name().equalsIgnoreCase(mode)) {
                return true;
            }
        }
        return false;
    }
}
