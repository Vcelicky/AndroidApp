package com.example.jozef.vcelicky;

public class NotificationInfo {

    private String viewTitleText;
    private String viewText;
    private String hiveName;
    private String hiveId;

    public NotificationInfo(String viewTitleText, String viewText, String hiveName, String hiveId) {
        this.viewTitleText = viewTitleText;
        this.viewText = viewText;
        this.hiveName = hiveName;
        this.hiveId = hiveId;
    }

    public String getViewTitleText() {
        return viewTitleText;
    }

    public void setViewTitleText(String viewTitleText) {
        this.viewTitleText = viewTitleText;
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }

    public String getHiveName() {
        return hiveName;
    }

    public void setHiveName(String hiveName) {
        this.hiveName = hiveName;
    }

    public String getHiveId() {
        return hiveId;
    }

    public void setHiveId(String hiveId) {
        this.hiveId = hiveId;
    }
}
