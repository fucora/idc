package com.iwellmass.idc.executor;

public interface IDCJob {



    // ~~ META DATA ~~
    String getContentType();

    // ~~ 业务逻辑 ~~
    void execute(IDCJobContext context);

}
