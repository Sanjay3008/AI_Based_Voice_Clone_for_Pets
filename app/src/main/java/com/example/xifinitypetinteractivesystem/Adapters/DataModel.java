package com.example.xifinitypetinteractivesystem.Adapters;

import java.io.File;

public class DataModel
{
    private final String Title;
    private final File file;

    public DataModel(String title, File file) {
        Title = title;
        this.file = file;
    }

    public String getTitle() {
        return Title;
    }

    public File getFile() {
        return file;
    }
}
