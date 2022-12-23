package com.heytap.ad.osync.core.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@AllArgsConstructor
public enum ExpState {

    /**
     * The Default.
     */
    DEFAULT(100, " default interrupt");

    private Integer code;

    private String desc;

}
