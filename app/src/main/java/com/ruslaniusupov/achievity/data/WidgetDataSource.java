package com.ruslaniusupov.achievity.data;

import com.ruslaniusupov.achievity.data.model.Note;

import java.util.List;

public interface WidgetDataSource {

    void addNotes(int widgetId, List<Note> notes);

    List<Note> getNotes(int widgetId);

    void deleteNotes(int widgetId);

}
