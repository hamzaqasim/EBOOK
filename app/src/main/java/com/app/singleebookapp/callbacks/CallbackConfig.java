package com.app.singleebookapp.callbacks;

import com.app.singleebookapp.models.Ads;
import com.app.singleebookapp.models.App;
import com.app.singleebookapp.models.Chapter;
import com.app.singleebookapp.models.Url;

import java.util.ArrayList;
import java.util.List;

public class CallbackConfig {

    public App app = null;
    public Url url = null;
    public Ads ads = null;
    public List<Chapter> table_of_contents = new ArrayList<>();

}