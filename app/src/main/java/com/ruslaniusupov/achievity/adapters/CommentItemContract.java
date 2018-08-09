package com.ruslaniusupov.achievity.adapters;

import com.ruslaniusupov.achievity.data.model.Comment;

interface CommentItemContract {

    interface View {

        void showText(String text);

        void showAuthor(String author);

        void showPublishDate(String publishDate);

        void showLikeCount(String count);

        void setFavBtnState(boolean isSelected);

    }

    interface Presenter {

        void updateUi(Comment comment);

        void like(String noteId, String commentId);

        void cancelLike(String noteId, String commentId);

    }

}
