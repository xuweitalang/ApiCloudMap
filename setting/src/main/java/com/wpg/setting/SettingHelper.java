package com.wpg.setting;


import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * @Author: xuwei
 * @Date: 2021/3/29 10:55
 * @Description:
 */
public class SettingHelper extends UZModule {
    public SettingHelper(UZWebView webView) {
        super(webView);
    }

    /**
     * 跳转到设置页面
     *
     * @param moduleContext
     */
    public void jsmethod_startSettingActivity(final UZModuleContext moduleContext) {
        KeepLiveUtils.goKeepLiveSetting(context());
    }
}
