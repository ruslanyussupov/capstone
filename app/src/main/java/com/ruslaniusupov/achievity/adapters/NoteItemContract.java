package com.ruslaniusupov.achievity.adapters;

import com.ruslaniusupov.achievity.data.model.Note;

interface NoteItemContract {

    interface View {

        void showAuthor(String author);

        void showPubDate(String date);

        void showText(String text);

        void showLikesCount(String count);

        void showCommentsCount(String count);

        void setLikeBtnState(boolean isSelected);

    }

    interface Presenter {

        void updateUi(Note note);

        void like(String noteId);

        void cancelLike(String noteId);

    }
}
